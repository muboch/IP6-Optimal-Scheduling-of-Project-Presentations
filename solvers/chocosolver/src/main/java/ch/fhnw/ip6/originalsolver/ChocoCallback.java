package ch.fhnw.ip6.originalsolver;

import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import java.util.List;




public class ChocoCallback {


    private int solutionCount;

    public ChocoCallback() {
        this.solutionCount = 0;
    }

    public void OnChocoCallback(Model model, IntVar[][] presRoomTime, List<P> presentations, List<R> rooms, List<T> timeslots, List<L> lecturers) {
        solutionCount++;
        Planning planning = new Planning();
        planning.setTimeslots(timeslots);
        planning.setRooms(rooms);

        for (T t : timeslots) {
            for (R r : rooms) {
                for (P p : presentations) {
                    if (presRoomTime[presentations.indexOf(p)][timeslots.indexOf(t)] == null) continue;
                    if (presRoomTime[presentations.indexOf(p)][timeslots.indexOf(t)].getValue() != -1 ) {
                        planning.getSolutions().add(new Solution(r, t, p, p.getExpert(), p.getCoach()));
                    }
                }
            }
        }
        System.out.println("Solution " + solutionCount);
        System.out.println(planning.getPlanningStats());
        System.out.println(planning.toString());
    }
}