package ch.fhnw.ip6.cpsolver;

import ch.fhnw.ip6.api.AbstractSolver;
import ch.fhnw.ip6.api.SolverApi;
import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Planning;
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
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static ch.fhnw.ip6.common.util.CostUtil.ROOM_SWITCH_COST;
import static ch.fhnw.ip6.common.util.CostUtil.USED_ROOM_COST;

@Component("ch.fhnw.ip6.cpsolver.Solver")
public class Solver extends AbstractSolver {

    @Value("${ospp.timelimit}")
    private int timelimit = 180;

    static {
        System.loadLibrary("jniortools");
    }

    public Solver(SolverContext solverContext) {
        super(solverContext);
    }

    @Override
    public Planning testSolve() {
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
        public Planning solve(List<Presentation> presentations, List<Lecturer> lecturers, List<Room> rooms, List<Timeslot> timeslots) {
        solverContext.setSolving(true);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CpModel model = new CpModel();

        presentations.forEach(System.out::println);
        rooms.forEach(System.out::println);
        timeslots.forEach(System.out::println);
        lecturers.forEach(System.out::println);

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

        // Data structures for Objectives
        ArrayList<IntVar> objIntVars = new ArrayList<IntVar>();
        ArrayList<Integer> objIntCoeffs = new ArrayList<>();



        // START CONSTRAINT:  For each Presentation, there must be 1 (room,timeslot) pair. -> Each presentation must be presented in a room at a time
        for (Presentation p : presentations) {
            List<IntVar> temp = new ArrayList<>();
            for (Timeslot t : timeslots) {
                for (Room r : rooms) {
                    if (presRoomTime[p.getId()][r.getId()][t.getId()] == null) continue;

                    temp.add(presRoomTime[p.getId()][r.getId()][t.getId()]);
                }
            }
            IntVar[] arr = temp.toArray(new IntVar[0]);
            // next line same as c#: "model.Add(LinearExpr.Sum(temp) == 1);"
            model.addLinearConstraint(LinearExpr.sum(arr), 1, 1); // SUM OF ALL MUST EQUAL ONE
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
                IntVar[] arr = temp.toArray(new IntVar[0]);
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
                IntVar[] arr = temp.toArray(new IntVar[0]);
                model.addLinearConstraint(LinearExpr.sum(arr), 0, 1); // <=1 -> max one out of overlap is allowed
            }
        }
        // END CONSTRAINT


        // START CONSTRAINT Soft Constraint 1. Coaches should switch the rooms as little as possible
        // Create (lecturer,room) booleans, minimize
        IntVar[][] coachRoom = new IntVar[lecturers.size()][rooms.size()];
        int[] coachRoomCost = new int[rooms.size()];
        for (Lecturer l : lecturers) {
            for (Room r : rooms) {
                coachRoom[l.getId()][r.getId()]  = model.newBoolVar("coach_"+l.getId()+"room_"+r.getId());
                coachRoomCost[r.getId()] = ROOM_SWITCH_COST;
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
                IntVar[] arr = temp.toArray(new IntVar[0]);

                // Implement coachRoom[l][r] == (sum(arr) >= 1).
                model.addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(coachRoom[l.getId()][r.getId()]);
                model.addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(coachRoom[l.getId()][r.getId()].not());

                // Add to objective
                objIntVars.add(coachRoom[l.getId()][r.getId()]);
                objIntCoeffs.add(coachRoomCost[r.getId()]);

            }
            // Minimize the amount of rooms a lecturer uses
            //model.minimize(LinearExpr.scalProd(coachRoom[l.getId()], coachRoomCost));
            // Add cost to model
        }
        // END CONSTRAINT

        // START CONSTRAINT 2. Coaches should have as little free timeslots between presentations as possible.
        IntVar[][] lecturerTimeslot = new IntVar[lecturers.size()][timeslots.size()]; // Coach has a presentation at timeslot
        int[] timeslotCost = new int[timeslots.size()];

        IntVar[] firstTimeslots = new IntVar[lecturers.size()];
        IntVar[] lastTimeslots = new IntVar[lecturers.size()];

        for (Lecturer l: lecturers) {

        }

            // Differenz = # stunden an dem lecturer anwesend sein muss
        // implication


        for (Lecturer l: lecturers){
                for (Timeslot t: timeslots){
                    lecturerTimeslot[l.getId()][t.getId()] = model.newBoolVar("lecturerTimeslot_"+l.getId()+"_" + t.getId());

                    timeslotCost[t.getId()] = t.getPriority();
                    ArrayList<IntVar> temp = new ArrayList<>();

                    for (Room r:rooms){

                        for (Presentation p: presentations){
                        if (presRoomTime[p.getId()][r.getId()][t.getId()] == null) continue;
                        temp.add(presRoomTime[p.getId()][r.getId()][t.getId()]);
                    }
                }
                    IntVar[] arr = temp.toArray(new IntVar[0]);
                    // Implement lecturerTimeslot[c][t] == (sum(arr) >= 1)
                    model.addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(lecturerTimeslot[l.getId()][t.getId()]);
                    model.addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(lecturerTimeslot[l.getId()][t.getId()].not());

                    //Add Objective
                    objIntVars.add(lecturerTimeslot[l.getId()][t.getId()]);
                    objIntCoeffs.add(timeslotCost[t.getId()]);
            }

/*
                for (Timeslot t: timeslots){
                     firstTimeslots[l.getId()] = model.newIntVar(0, timeslots.size(), "firstTimeslot"+l.getId());
                     model.addMinEquality(firstTimeslots[l.getId()], [item for item in range()])

                }
*/




            //model.minimize(LinearExpr.scalProd(lecturerTimeslot[l.getId()], timeslotCost));

        }


        // END CONSTRAINT


        // START CONSTRAINT 3.1 As little rooms as possible should be free per timeslots -> Minimize used Timeslots
        IntVar[] timeslotUsed = new IntVar[timeslots.size()];
        for (Timeslot t : timeslots) {
            timeslotUsed[t.getId()] = model.newBoolVar("timeslotUsed_" + t.getId());
        }

        for (Timeslot t : timeslots) {
            List<IntVar> temp = new ArrayList<IntVar>();

            for (Room r : rooms) {
                for (Presentation p : presentations) {
                    if (presRoomTime[p.getId()][r.getId()][t.getId()] == null) continue;
                    temp.add(presRoomTime[p.getId()][r.getId()][t.getId()]);
                }
            }
            IntVar[] arr = temp.toArray(new IntVar[0]);
            /// IF SUM ARR > 0 add boolean timeslotUsed TRUE else FALSE;
            // Implement timeslotUsed[t] == (sum(arr) >= 1).
            model.addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(timeslotUsed[t.getId()]);
            model.addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(timeslotUsed[t.getId()].not());

            //Add Objective
            objIntVars.add(timeslotUsed[t.getId()]);
            objIntCoeffs.add(timeslotCost[t.getId()]);
        }
        //model.minimize(LinearExpr.scalProd(timeslotUsed, timeslotCost));
        // END CONSTRAINT

        // START CONSTRAINT 4 As little rooms as possible should be used over all -> Minimize used Rooms over all timeslots
        IntVar[] roomUsed = new IntVar[rooms.size()];
        int[] roomCost = new int[rooms.size()];
        for (Room r : rooms) {
            roomUsed[r.getId()] = model.newBoolVar("roomUsed_" + r.getId());
            roomCost[r.getId()] = USED_ROOM_COST;
        }

        for (Room r : rooms) {
            List<IntVar> temp = new ArrayList<IntVar>();

            for (Timeslot t : timeslots) {
                for (Presentation p : presentations) {
                    if (presRoomTime[p.getId()][r.getId()][t.getId()] == null) continue;
                    temp.add(presRoomTime[p.getId()][r.getId()][t.getId()]);
                }
            }
            IntVar[] arr = temp.toArray(new IntVar[0]);
            /// IF SUM ARR > 0 add boolean roomUsed TRUE else FALSE;
            // Implement timeslotUsed[t] == (sum(arr) >= 1).
            model.addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(roomUsed[r.getId()]);
            model.addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(roomUsed[r.getId()].not());

            //Add Objective
            objIntVars.add(roomUsed[r.getId()]);
            objIntCoeffs.add(roomCost[r.getId()]);
        }
        //model.minimize(LinearExpr.scalProd(roomUsed,roomCost));
        // END CONSTRAINT


        // Add the objective to the Solver, parse to array first because java is funny like that
        int[] objIntCoeffsArr = objIntCoeffs.stream().mapToInt(i->i).toArray(); // objIntCoeffs.toArray(int[]::new);
        IntVar[] objIntVarsArr = objIntVars.toArray(new IntVar[0]);

        // finally, minimize the objective
        model.minimize(LinearExpr.scalProd(objIntVarsArr, objIntCoeffsArr));

        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(timelimit);
        System.out.println("All constraints done, solving");
        System.out.println(model.validate());
        PresentationSolutionObserver cb = new PresentationSolutionObserver(presRoomTime, lecturers, presentations, timeslots, rooms, stopWatch, solverContext);

        CpSolverStatus res = solver.searchAllSolutions(model, cb);
        System.out.println(res);

        stopWatch.stop();
        Planning p =  solverContext.getPlanning();
        p.setStatus(res.name());
        solverContext.setSolving(false);
        return p;
    }
}
