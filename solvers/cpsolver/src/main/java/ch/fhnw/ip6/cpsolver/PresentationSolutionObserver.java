package ch.fhnw.ip6.cpsolver;

import ch.fhnw.ip6.common.classes.*;
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

    @Override
    public void onSolutionCallback() {
        solutionCount++;
        if (!this.to_print.contains(solutionCount)) return;
        var results = new ArrayList<Solution>();
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
                        results.add(new Solution(r, t, p, p.getExpert(), p.getCoach()));
                        professorInfo.add("Pres {p.Id} has Professor {p.Supervisor.Id}, Expert {p.Expert.Id} at time {t.Datum}, room {r.Id}");
                    }
                }
            }
        }

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
