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

    public PresentationSolutionObserver(IntVar[][][] presRoomTime, List<Lecturer> lecturers, List<Presentation> presentations, List<Timeslot> timeslots, List<Room> rooms, StopWatch stopWatch, SolverContext solverContext) {
        this.presRoomTime = presRoomTime;
        this.lecturers = lecturers;
        this.presentations = presentations;
        this.timeslots = timeslots;
        this.rooms = rooms;
        this.solutionCount = 0;
        this.stopWatch = stopWatch;
        this.solverContext = solverContext;

    }
}
