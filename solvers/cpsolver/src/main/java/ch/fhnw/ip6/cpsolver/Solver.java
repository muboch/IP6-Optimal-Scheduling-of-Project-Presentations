package ch.fhnw.ip6.cpsolver;

import ch.fhnw.ip6.api.SolverApi;
import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.Timeslot;
import ch.fhnw.ip6.common.util.JsonUtil;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component("ch.fhnw.ip6.cpsolver.Solver")
public class Solver implements SolverApi {

    @Value("${ospp.timelimit}")
    private int timelimit = 10;

    static {
        System.loadLibrary("jniortools");
    }

    @Override
    public Solution testSolve() {
        JsonUtil util = new JsonUtil();

        List<Presentation> presentations = util.getJsonAsList("presentations.json", Presentation.class);
        List<Lecturer> lecturers = util.getJsonAsList("lecturers.json", Lecturer.class);
        List<Room> rooms = util.getJsonAsList("rooms.json", Room.class).stream().filter(r -> r.getReserve().equals(false)).collect(Collectors.toList());
        List<Timeslot> timeslots = util.getJsonAsList("timeslots.json", Timeslot.class);

        for (Presentation p : presentations) {
            p.setCoach(lecturers.stream().filter(t -> t.getInitials().equals(p.getCoachInitials())).findFirst().get()); // Assign Coaches to Presentation
            p.setExpert(lecturers.stream().filter(t -> t.getInitials().equals(p.getExpertInitials())).findFirst().get()); // Assign Experts to Presentation
        }

        return solve(presentations, lecturers, rooms, timeslots);
    }

    @Override
    public Solution solve(List<Presentation> presentations, List<Lecturer> lecturers, List<Room> rooms, List<Timeslot> timeslots) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CpModel model = new CpModel();

        for (Presentation p : presentations) {
            p.setCoach(lecturers.stream().filter(t -> t.getInitials().equals(p.getCoachInitials())).findFirst().get()); // Assign Coaches to Presentation
            p.setExpert(lecturers.stream().filter(t -> t.getInitials().equals(p.getExpertInitials())).findFirst().get()); // Assign Experts to Presentation
        }

        //Create model. presTimeRoom[p,t,r] == 1 -> Presentation p happens in room r at time t
        IntVar[][][] presRoomTime = new IntVar[presentations.size()][rooms.size()][timeslots.size()];
        for (Timeslot t : timeslots) {
            for (Room r : rooms) {
                for (Presentation p : presentations) {
                    if (!p.getType().equals(r.getType())) {
                        continue;
                    }
                    presRoomTime[p.getId()][r.getId()][t.getId()] = model.newBoolVar("presRoomTime_p" + p.getId() + "_r" + r.getId() + "_t" + t.getId());
                }
            }
        }

        System.out.println("Setup completed");
        // For each presentations, list the presentations that are not allowed to overlap
        List<Presentation>[] presentationsPerLecturer = new ArrayList[lecturers.size()];
        for (Lecturer l : lecturers) {
            presentationsPerLecturer[l.getId()] = presentations.stream().filter(ps -> ps.getExpert().getId() == l.getId() || ps.getCoach().getId() == l.getId()).collect(Collectors.toList());
        }
        System.out.println("Overlap calculation completed");

        // START CONSTRAINT:  For each Presentation, there must be 1 (room,timeslot) pair. -> Each presentation must be presented in a room at a time
        for (Presentation p : presentations) {
            List<IntVar> temp = new ArrayList<>();
            for (Timeslot t : timeslots) {
                for (Room r : rooms) {
                    if (presRoomTime[p.getId()][r.getId()][t.getId()] == null) continue;

                    temp.add(presRoomTime[p.getId()][r.getId()][t.getId()]);
                }
            }
            IntVar[] arr = temp.toArray(IntVar[]::new);
            // next line same as c#: "model.Add(LinearExpr.Sum(temp) == 1);"
            model.addLinearConstraint(LinearExpr.sum(arr), 1, 1); // SUM OF ALL MUST EQUAL ONE ?????
        }
        // END CONSTRAINT

        // START CONSTRAINT For each (room, timeslot) pair there must be <=1 presentation -> Max 1 Presentation per Room/Time
        for (Room r : rooms) {
            for (Timeslot t : timeslots) {
                List<IntVar> temp = new ArrayList<IntVar>();
                for (Presentation p : presentations) {
                    if (presRoomTime[p.getId()][r.getId()][t.getId()] == null) continue;
                    temp.add(presRoomTime[p.getId()][r.getId()][t.getId()]);
                }
                IntVar[] arr = temp.toArray(IntVar[]::new);
                model.addLinearConstraint(LinearExpr.sum(arr), 0, 1);
            }
        }
        // END CONSTRAINT


        // START CONSTRAINT Foreach presentation, the following conflicting (presentation,room, time) pairs are not allowed -> Lecturers may not have more than one presentation at a time.
        for (Lecturer l : lecturers) {
            for (Timeslot t : timeslots) {
                List<IntVar> temp = new ArrayList<>();
                for (Room r : rooms) {
                    for (Presentation p1 : presentationsPerLecturer[l.getId()]) {
                        if (presRoomTime[p1.getId()][r.getId()][t.getId()] == null) continue;
                        temp.add(presRoomTime[p1.getId()][r.getId()][t.getId()]);
                    }
                }
                IntVar[] arr = temp.toArray(IntVar[]::new);
                model.addLinearConstraint(LinearExpr.sum(arr), 0, 1); // <=1 -> max one out of overlap is allowed
            }
        }
        // END CONSTRAINT

        // START CONSTRAINT Soft Constraint 1. Coaches should switch the rooms as little as possible
        // Create (lecturer,room) booleans, minimize
        IntVar[][] coachRoom = new IntVar[lecturers.size()][rooms.size()];
        for (Lecturer l : lecturers) {
            for (Room r : rooms) {
                coachRoom[l.getId()][r.getId()]  = model.newBoolVar("coach_"+l.getId()+"room_"+r.getId()); //
            }
        }
        for (Lecturer l : lecturers) {
            for (Room r : rooms) {
                List<IntVar> temp = new ArrayList<>();

                for (Presentation p1 : presentationsPerLecturer[l.getId()]) {
                    for (Timeslot t : timeslots) {
                        if (presRoomTime[p1.getId()][r.getId()][t.getId()] == null) continue;
                        temp.add(presRoomTime[p1.getId()][r.getId()][t.getId()]);
                    }
                }
                IntVar[] arr = temp.toArray(IntVar[]::new);

                // Implement coachRoom[l][r] == (sum(arr) >= 1).
                model.addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(coachRoom[l.getId()][r.getId()]);
                model.addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(coachRoom[l.getId()][r.getId()].not());
            }
            // Minimize the amount of rooms a lecturer uses
            model.minimize(LinearExpr.sum(coachRoom[l.getId()]));
        }
        // END CONSTRAINT

        // START CONSTRAINT 2. Coaches should have as little free timeslots between presentations as possible.
        IntVar[][] coachAtTimeslot = new IntVar[lecturers.size()][timeslots.size()]; // Coach has a presentation at timeslot
        //   var firstTimeslot =


        // END CONSTRAINT


        // START CONSTRAINT 3.1 As little rooms as possible should be free per timeslots -> Minimize used Timeslots
        IntVar[] timeslotUsed = new IntVar[timeslots.size()];
        int[] timeslotCost = new int[timeslots.size()];
        for (Timeslot t : timeslots) {
            timeslotUsed[t.getId()] = model.newBoolVar("timeslotUsed_" + t.getId());
            timeslotCost[t.getId()] = t.getId();
        }

        for (Timeslot t : timeslots) {
            List<IntVar> temp = new ArrayList<IntVar>();

            for (Room r : rooms) {
                for (Presentation p : presentations) {
                    if (presRoomTime[p.getId()][r.getId()][t.getId()] == null) continue;
                    temp.add(presRoomTime[p.getId()][r.getId()][t.getId()]);
                }
            }
            IntVar[] arr = temp.toArray(IntVar[]::new);
            /// IF SUM ARR > 0 add boolean timeslotUsed TRUE else FALSE;
            // Implement timeslotUsed[t] == (sum(arr) >= 1).
            model.addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(timeslotUsed[t.getId()]);
            model.addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(timeslotUsed[t.getId()].not());
        }
        model.minimize(LinearExpr.scalProd(timeslotUsed, timeslotCost));


        // END CONSTRAINT

        //START CONSTRAINT 3.2 As little rooms as possible should be free per timeslots -> Maximize used rooms per timeslot
        /*
        IntVar[][] timeslotRooms = new IntVar[timeslots.size()][rooms.size()];
        for (var t : timeslots) {
            for (var r : rooms) {
                timeslotRooms[t.getId()][r.getId()] = model.newBoolVar("timeslotRooms_" + t.getId()+"_"+r.getId());
            }
        }

        for (var t : timeslots){
            for (var r: rooms){
                var temp = new ArrayList<IntVar>();

                for (var p: presentations){
                    if (presRoomTime[p.getId()][r.getId()][t.getId()] == null) continue;
                    temp.add(presRoomTime[p.getId()][r.getId()][t.getId()]);
                }
                IntVar[] arr = temp.toArray(IntVar[]::new);
                // Implement timeslotRooms[t][r] == (sum(arr) >= 1).
                model.addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(timeslotRooms[t.getId()][r.getId()]);
                model.addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(timeslotRooms[t.getId()][r.getId()].not());
            }
            model.maximize(LinearExpr.sum(timeslotRooms[t.getId()])); // Maximize rooms for timeslot
        }

         */





        HashSet<Integer> to_print = new HashSet<Integer>();
        to_print.add(0);
        to_print.add(1);
        to_print.add(2);
        to_print.add(100);
        to_print.add(200);
        to_print.add(600);
        to_print.add(1000);

        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(timelimit);
        System.out.println("All constraints done, solving");
        System.out.println(model.validate());
        PresentationSolutionObserver cb = new PresentationSolutionObserver(presRoomTime, lecturers, presentations, timeslots, rooms,
                to_print, stopWatch);

        CpSolverStatus res = solver.searchAllSolutions(model, cb);
        System.out.println(res);

        stopWatch.stop();
        return null;
    }
}
