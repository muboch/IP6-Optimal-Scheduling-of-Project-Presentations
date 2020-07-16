package ch.fhnw.ip6.ilpsolver.callback;

import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.ilpsolver.ILPModel;
import ch.fhnw.ip6.solutionchecker.SolutionChecker;
import gurobi.GRB;
import gurobi.GRBCallback;
import gurobi.GRBVar;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class ILPSolverCallback extends GRBCallback {

    private final GRBVar[][][] x;
    private final List<P> presentations;
    private final List<T> timeslots;
    private final List<R> rooms;
    private final List<L> lecturers;
    private final SolutionChecker solutionChecker;
    private final SolverContext solverContext;

    @SneakyThrows
    @Override
    protected void callback() {

        if (where == GRB.CB_MIPSOL) {

            Planning planning = new Planning();
            planning.setRooms(rooms);
            planning.setTimeslots(timeslots);
            planning.setNr(solverContext.getPlanning() != null ? solverContext.getPlanning().getNr() + 1 : 1);
            Set<Solution> solutions = new HashSet<>();
            for (P p : presentations) {
                for (T t : timeslots) {
                    for (R r : rooms) {
                        if (x[presentations.indexOf(p)][timeslots.indexOf(t)][rooms.indexOf(r)] == null) continue;
                        if (getSolution(x[presentations.indexOf(p)][timeslots.indexOf(t)][rooms.indexOf(r)]) == 1.0) {
                            solutions.add(new Solution(r, t, p, p.getCoach(), p.getExpert()));
                        }
                    }
                }
            }
            planning.setSolutions(solutions);
            solutionChecker.generateStats(planning, lecturers, presentations, timeslots, rooms);
            planning.setCost(solutionChecker.getTotalPlanningCost());
            solverContext.saveBestPlanning(planning);
            log.debug("New Planning Nr. " + planning.getNr() + " - Cost: " + planning.getCost() + "\n" + planning.getPlanningStats() + "\n" + planning.getPlanningAsTable());
        }


    }

    public ILPSolverCallback(ILPModel model, SolverContext context) {
        this.x = model.getX();
        this.presentations = model.getPresentations();
        this.timeslots = model.getTimeslots();
        this.rooms = model.getRooms();
        this.lecturers = model.getLecturers();
        this.solutionChecker = new SolutionChecker();
        this.solverContext = context;
    }
}
