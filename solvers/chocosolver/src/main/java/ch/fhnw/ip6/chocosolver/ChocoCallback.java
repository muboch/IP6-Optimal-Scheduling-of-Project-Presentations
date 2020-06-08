package ch.fhnw.ip6.chocosolver;

import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.solutionchecker.SolutionChecker;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.util.List;


public class ChocoCallback {


    private int solutionCount;

    public ChocoCallback() {
        this.solutionCount = 0;
    }

    public void OnChocoCallback(Model model, IntVar[][] presRoomTime, List<P> presentations, List<R> rooms, List<T> timeslots, List<L> lecturers, SolutionChecker solutionChecker) {
        solutionCount++;
        Planning planning = new Planning();
        planning.setTimeslots(timeslots);
        planning.setRooms(rooms);
        for (R r : rooms) {
            for (T t : timeslots) {
                if (presRoomTime[timeslots.indexOf(t)][rooms.indexOf(r)].getValue() != -1) {
                    P p = presentations.stream().filter(pres -> pres.getId() == presRoomTime[timeslots.indexOf(t)][rooms.indexOf(r)].getValue()).findFirst().get();
                    planning.getSolutions().add(new Solution(r, t, p, p.getExpert(), p.getCoach()));
                }

            }
        }
        solutionChecker.generateStats(planning, lecturers, presentations, timeslots, rooms);
        planning.setCost(solutionChecker.getTotalPlanningCost());

        System.out.println(planning.getPlanningStats());

        System.out.println();
        System.out.println("Planning Nr:    " + planning.getNr());
        System.out.println(planning.getPlanningAsTable());
        //solverContext.saveBestPlanning(planning);

    }
}