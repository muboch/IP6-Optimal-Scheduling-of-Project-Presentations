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
import gurobi.GRBModel;
import gurobi.GRBVar;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ILPSolverCallback extends GRBCallback {

    private final GRBVar[][][] x;
    private final List<P> presentations;
    private final List<T> timeslots;
    private final List<R> rooms;
    private final List<L> lecturers;
    private final SolutionChecker solutionChecker;
    private final SolverContext solverContext;

    private final Logger log = LoggerFactory.getLogger(ILPSolverCallback.class);

    @SneakyThrows
    @Override
    protected void callback() {

        if (where == GRB.CB_MIPSOL) {

            Planning planning = new Planning();
            planning.setRooms(rooms);
            planning.setTimeslots(timeslots);
            Set<Solution> solutions = new HashSet<>();
            for (P p : presentations) {
                for (T t : timeslots) {
                    for (R r : rooms) {
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
            log.info("New Planning Nr. {} - Cost: {}\n{}\n{}", planning.getNr(), planning.getCost(),planning.getPlanningStats(), planning.getPlanningAsTable());

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
