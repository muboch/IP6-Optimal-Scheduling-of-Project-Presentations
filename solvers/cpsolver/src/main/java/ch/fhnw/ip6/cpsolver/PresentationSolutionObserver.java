package ch.fhnw.ip6.cpsolver;

import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.Timeslot;
import ch.fhnw.ip6.solutionchecker.SolutionChecker;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.IntVar;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PresentationSolutionObserver extends CpSolverSolutionCallback {
    private final IntVar[][][] presRoomTime;
    private final List<Presentation> presentations;
    private final List<Lecturer> lecturers;
    private final List<Timeslot> timeslots;
    private final List<Room> rooms;
    private final HashSet<Integer> to_print;
    private final StopWatch stopWatch;
    private int solutionCount;
    private static SolutionChecker solutionChecker = new SolutionChecker();

    @Override
    public void onSolutionCallback() {
        solutionCount++;

        //if (!this.to_print.contains(solutionCount)) return;
        System.out.println("Solution "+ solutionCount +" . Time: "+ stopWatch.getTime());
        var solutions = new ArrayList<Solution>();
        var professorInfo = new ArrayList<String>();

        for (var t : timeslots)
        {
            for (var r : rooms)
            {
                for (var p : presentations)
                {
                    if (presRoomTime[p.getId()][r.getId()][t.getId()] == null) continue;
                    if (booleanValue(presRoomTime[p.getId()][r.getId()][t.getId()]))
                    {
                        solutions.add(new Solution(r, t, p, p.getExpert(), p.getCoach()));
                        professorInfo.add("Pres {p.Id} has Professor {p.Supervisor.Id}, Expert {p.Expert.Id} at time {t.Datum}, room {r.Id}");
                    }
                }
            }
        }
        solutionChecker.printSolution(solutions,lecturers,presentations,timeslots,rooms,solutionCount);
        solutionChecker.checkSolutionForCorrectness(solutions,lecturers,presentations,timeslots,rooms);


    }

    public PresentationSolutionObserver(IntVar[][][] presRoomTime, List<Lecturer> lecturers, List<Presentation> presentations, List<Timeslot> timeslots, List<Room> rooms, HashSet<Integer> to_print, StopWatch stopWatch) {
    this.presRoomTime = presRoomTime;
    this.lecturers = lecturers;
    this.presentations = presentations;
    this.timeslots = timeslots;
    this.rooms = rooms;
    this.to_print = to_print;
    this.solutionCount = 0;
    this.stopWatch = stopWatch;
    }
}
