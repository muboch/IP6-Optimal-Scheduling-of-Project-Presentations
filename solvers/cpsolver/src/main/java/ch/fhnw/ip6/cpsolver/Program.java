package ch.fhnw.ip6.cpsolver;

import ch.fhnw.ip6.common.classes.Lecturer;
import ch.fhnw.ip6.common.classes.Presentation;
import ch.fhnw.ip6.common.classes.Room;
import ch.fhnw.ip6.common.classes.Timeslot;
import ch.fhnw.ip6.common.util.JsonUtil;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Program {

    static {
        System.setProperty("java.library.path", Objects.requireNonNull(Program.class.getClassLoader().getResource("libs/")).getPath());
        System.loadLibrary("jniortools");
    }

    public static void main(String[] args) {
        JsonUtil util = new JsonUtil();

        List<Presentation> presentations = util.getJsonAsList("presentations.json", Presentation.class);
        List<Lecturer> lecturers = util.getJsonAsList("lecturers.json", Lecturer.class);
        List<Room> rooms = util.getJsonAsList("rooms.json", Room.class);
        List<Timeslot> timeslots = util.getJsonAsList("timeslots.json", Timeslot.class);

        presentations.forEach(System.out::println);
        lecturers.forEach(System.out::println);
        rooms.forEach(System.out::println);
        timeslots.forEach(System.out::println);


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CpModel model = new CpModel();

        for (Presentation p : presentations) {
            p.setCoach(lecturers.stream().filter(t -> t.getInitials().equals(p.getCoachInitials())).findFirst().get()); // Assign Coaches to Presentation
            p.setExpert(lecturers.stream().filter(t -> t.getInitials().equals(p.getExpertInitials())).findFirst().get()); // Assign Experts to Presentation
        }

        //Create model. presTimeRoom[p,t,r] -> Presentation p happens in room r at time t
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
        List<Presentation>[] nonOverlappingPresentations = new List[lecturers.size()];
        for (Lecturer l : lecturers) {
            nonOverlappingPresentations[l.getId()] = presentations.stream().filter(ps -> ps.getExpert().getId() == l.getId() || ps.getCoach().getId() == l.getId()).collect(Collectors.toList());
        }
        System.out.println("Overlap calculation completed");

        // START CONSTRAINT:  For each Presentation, there must be 1 (room,timeslot) pair. -> Each presentation must be presented in a room at a time
        for (var p : presentations) {
            var temp = new ArrayList<IntVar>();
            for (var t : timeslots) {
                for (var r : rooms) {
                    if (presRoomTime[p.getId()][r.getId()][t.getId()] == null) continue;

                    temp.add(presRoomTime[p.getId()][r.getId()][t.getId()]);
                }
            }
            IntVar[] arr =  temp.toArray(IntVar[]::new);
            // next line same as c#: "model.Add(LinearExpr.Sum(temp) == 1);"
            model.addLinearConstraint(LinearExpr.sum(arr), 1, 1); // SUM OF ALL MUST EQUAL ONE ?????
        }
        // END CONSTRAINT

        // START CONSTRAINT For each (room, timeslot) pair there must be <=1 presentation -> Max 1 Presentation per Room/Time
        for (var r : rooms) {
            for (var t : timeslots) {
                var temp = new ArrayList<IntVar>();
                for (var p : presentations) {
                    if (presRoomTime[p.getId()][r.getId()][t.getId()] == null) continue;
                    temp.add(presRoomTime[p.getId()][r.getId()][t.getId()]);
                }
                IntVar[] arr = temp.toArray(IntVar[]::new);
                model.addLinearConstraint(LinearExpr.sum(arr), 0, 1);
            }
        }
        // END CONSTRAINT


        // START CONSTRAINT Foreach presentation, the following conflicting (presentation,room, time) pairs are not allowed
        for (var l : lecturers) {

            for (var t : timeslots) {
                var temp = new ArrayList<IntVar>();
                for (var r : rooms) {
                    for (var p1 : nonOverlappingPresentations[l.getId()]) {
                        temp.add(presRoomTime[p1.getId()][r.getId()][t.getId()]);
                    }
                    System.out.println("rr"+r.getId());

                }

                System.out.println(t.getId());
                IntVar[] arr = temp.toArray(IntVar[]::new);
                model.addLinearConstraint(LinearExpr.sum(arr), 0, 1); // <=1 -> max one out of overlap is allowed
            }
        }
        // END CONSTRAINT



        HashSet<Integer> to_print = new HashSet<Integer>();
        to_print.add(0);
        to_print.add(1);
        to_print.add(2);
        to_print.add(100);
        to_print.add(200);
        to_print.add(600);
        to_print.add(1000);

        CpSolver solver = new CpSolver();
        var cb = new PresentationSolutionObserver(presRoomTime, lecturers, presentations, timeslots, rooms,
                to_print);
        var res = solver.searchAllSolutions(model, cb);

        stopWatch.stop();
    }
}
