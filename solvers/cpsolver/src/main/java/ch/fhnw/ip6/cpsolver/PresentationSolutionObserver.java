package ch.fhnw.ip6.cpsolver;

import ch.fhnw.ip6.common.classes.*;
import ch.fhnw.ip6.solutionchecker.SolutionChecker;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.IntVar;

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
    private int solutionCount;
    private static SolutionChecker solutionChecker = new SolutionChecker();

    @Override
    public void onSolutionCallback() {
        solutionCount++;
        if (!this.to_print.contains(solutionCount)) return;
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



    }

    public PresentationSolutionObserver(IntVar[][][] presRoomTime, List<Lecturer> lecturers, List<Presentation> presentations, List<Timeslot> timeslots, List<Room> rooms, HashSet<Integer> to_print) {
    this.presRoomTime = presRoomTime;
    this.lecturers = lecturers;
    this.presentations = presentations;
    this.timeslots = timeslots;
    this.rooms = rooms;
    this.to_print = to_print;
    this.solutionCount = 0;
    }
}
