package ch.fhnw.ip6.cpsolver;

import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.Timeslot;
import ch.fhnw.ip6.solutionchecker.SolutionChecker;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.IntVar;
import org.apache.commons.lang3.time.StopWatch;

import java.util.List;

public class PresentationSolutionObserver extends CpSolverSolutionCallback {
    private final IntVar[][][] presRoomTime;
    private final List<Presentation> presentations;
    private final List<Lecturer> lecturers;
    private final List<Timeslot> timeslots;
    private final List<Room> rooms;
    private final StopWatch stopWatch;
    private final SolverContext solverContext;
    private final IntVar[] firstTimeslots;
    private final IntVar[][] lecturerTimeslots;
    private final IntVar[] lastTimeslots;
    private final IntVar[] diffs;
    private final IntVar[][] coachRoomTime;
    private final IntVar[][] roomDiffsInt;
    private final IntVar[][] roomDiffsBool;
    private final IntVar[] numChangesForLecturer;
    private final IntVar[][][] coachTimeRoomBool;
    private int solutionCount;
    private static SolutionChecker solutionChecker = new SolutionChecker();

    @Override
    public void onSolutionCallback() {
        solutionCount++;

        System.out.println("Solution " + solutionCount + " . Time: " + stopWatch.getTime());
        Planning planning = new Planning();
        planning.setNr(solutionCount);
        planning.setTimeslots(timeslots);
        planning.setRooms(rooms);
        /*
        for (Lecturer l : lecturers) {
            for (Timeslot t : timeslots) {
                System.out.println("Lecturer" + l.getId() + "has a pres at time " + t.getId() + ": " + booleanValue(lecturerTimeslots[l.getId()][t.getId()]));
            }
            System.out.println();
        }
        for (Lecturer l : lecturers) {
            for (Timeslot t : timeslots) {
                for (Room r : rooms) {
                    System.out.println("Lecturer" + l.getId() + "has a pres at time " + t.getId() + " in room " + r.getId()+": " + booleanValue(coachTimeRoomBool[l.getId()][t.getId()][r.getId()])
                    );
                }
                System.out.println();
            }
            System.out.println();
            System.out.println();

        }*/


        for (Lecturer l : lecturers) {
            for (Timeslot t : timeslots) {
                System.out.println("Lecturer" + l.getId() + "at time " + t.getId() + " has room " + value(coachRoomTime[l.getId()][t.getId()]));

            }
            System.out.println();
        }




        /*
        for (Lecturer l : lecturers) {
            //        System.out.println("First/Last pres for lecturer" + l.getId() + ": " + value(firstTimeslots[l.getId()]) + "/" + value(lastTimeslots[l.getId()]) + ". Diff: " + value(diffs[l.getId()]));
        }
        */


        for (Lecturer l : lecturers) {

            for (Timeslot t : timeslots) {
                System.out.println("RoomDiffsInt for lecturer " + l.getId() + " at time " + t.getId() + ": " + value(roomDiffsInt[l.getId()][t.getId()]));
                //System.out.println("RoomDiffsBool for lecturer " + l.getId() + " at time " + t.getId() + ": " + booleanValue(roomDiffsBool[l.getId()][t.getId()]));
            }

            System.out.println("numChangesForLecturer for lecturer " + l.getId() + ":" + value(numChangesForLecturer[l.getId()]));

        }


        for (Timeslot t : timeslots) {
            for (Room r : rooms) {
                for (Presentation p : presentations) {
                    if (presRoomTime[p.getId()][r.getId()][t.getId()] == null) continue;
                    if (booleanValue(presRoomTime[p.getId()][r.getId()][t.getId()])) {
                        planning.getSolutions().add(new Solution(r, t, p, p.getExpert(), p.getCoach()));
                    }
                }
            }
        }
        System.out.println("Solution " + solutionCount);
        planning.setCost(solutionChecker.getSolutionCost(planning.getSolutions(), lecturers, presentations, timeslots, rooms));
        System.out.println(planning.toString());
        solverContext.saveBestPlanning(planning);
    }

    public PresentationSolutionObserver(IntVar[][][] presRoomTime, List<Lecturer> lecturers, List<Presentation> presentations, List<Timeslot> timeslots, List<Room> rooms, StopWatch stopWatch, SolverContext solverContext, IntVar[] firstTimeslots, IntVar[][] lecturerTimeslots, IntVar[] lastTimeslots, IntVar[] diffs, IntVar[][] coachRoomTime, IntVar[][] roomDiffsInt, IntVar[][] roomDiffsBool, IntVar[] numChangesForLecturer, IntVar[][][] coachTimeRoomBool) {
        this.presRoomTime = presRoomTime;
        this.lecturers = lecturers;
        this.presentations = presentations;
        this.timeslots = timeslots;
        this.rooms = rooms;
        this.solutionCount = 0;
        this.stopWatch = stopWatch;
        this.solverContext = solverContext;

        this.firstTimeslots = firstTimeslots;
        this.lastTimeslots = lastTimeslots;
        this.lecturerTimeslots = lecturerTimeslots;
        this.diffs = diffs;
        this.coachRoomTime = coachRoomTime;

        this.roomDiffsInt = roomDiffsInt;
        this.roomDiffsBool = roomDiffsBool;
        this.numChangesForLecturer = numChangesForLecturer;
        this.coachTimeRoomBool = coachTimeRoomBool;
    }
}
