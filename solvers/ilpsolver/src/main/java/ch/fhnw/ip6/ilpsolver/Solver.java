package ch.fhnw.ip6.ilpsolver;

import ch.fhnw.ip6.api.AbstractSolver;
import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.ilpsolver.callback.ILPSolverCallback;
import ch.fhnw.ip6.ilpsolver.constraint.Constraint;
import ch.fhnw.ip6.ilpsolver.constraint.hard.AllPresentationsToRoomAndTimeslotAssigned;
import ch.fhnw.ip6.ilpsolver.constraint.hard.LecturerNotMoreThanOnePresentationPerTimeslot;
import ch.fhnw.ip6.ilpsolver.constraint.hard.OnlyOnePresentationPerRoomAndTimeslot;
import ch.fhnw.ip6.ilpsolver.constraint.soft.MinFreeTimeslots;
import ch.fhnw.ip6.ilpsolver.constraint.soft.MinRoomSwitches;
import ch.fhnw.ip6.ilpsolver.constraint.soft.MinRoomUsages;
import ch.fhnw.ip6.ilpsolver.constraint.soft.MinTimeslotUsages;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("ch.fhnw.ip6.ilpsolver.Solver")
public class Solver extends AbstractSolver {

    private final static Logger log = LoggerFactory.getLogger(Solver.class);

    public Solver(SolverContext solverContext) {
        super(solverContext);
    }

    @Override
    public Planning solve(List<P> ps, List<L> ls, List<R> rs, List<T> ts, boolean[][] offTimes) {

        // set this flag so other processes know that the solver is running
        solverContext.setSolving(true);
        StopWatch watch = new StopWatch();
        watch.start();
        try {

            GRBEnv env = new GRBEnv();
            GRBModel grbModel = new GRBModel(env);
            grbModel.set(GRB.StringAttr.ModelName, "ospp-fms");

            ILPModel model = new ILPModel(ps, ls, rs, ts, offTimes, grbModel);

            log.info("Number of Problem Instances: Presentations: {}, Lecturers: {}, Rooms: {}, Timeslots: {}, OffTimes: {}", ps.size(), ls.size(), rs.size(), ts.size(), offTimes.length);

            GRBLinExpr objective = new GRBLinExpr();

            watch.split();
            List<Constraint> constraints = new ArrayList<>();
            constraints.add(new AllPresentationsToRoomAndTimeslotAssigned());
            constraints.add(new LecturerNotMoreThanOnePresentationPerTimeslot());
            constraints.add(new OnlyOnePresentationPerRoomAndTimeslot());
            constraints.add(new MinTimeslotUsages());
            constraints.add(new MinRoomUsages());
            constraints.add(new MinFreeTimeslots());
            constraints.add(new MinRoomSwitches());
            constraints.forEach(c -> {
                c.setObjectives(objective);
                c.setModel(model).build();
            });
            log.debug("Setup Constraints duration: {}ms", watch.getSplitTime());
            watch.unsplit();

            grbModel.setCallback(new ILPSolverCallback(model, solverContext));
            grbModel.setObjective(objective);
            grbModel.set(GRB.IntParam.LogToConsole, 1);
            grbModel.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
            grbModel.set(GRB.DoubleParam.TimeLimit, timeLimit);
            grbModel.update();

            watch.split();
            log.debug("Start with Gurobi Optimization");
            grbModel.optimize();
            log.debug("End of Gurobi Optimization after {}ms", watch.getSplitTime());
            watch.unsplit();

            Planning planning = solverContext.getPlanning();
            planning.setTimeslots(ts);
            planning.setRooms(rs);
            fillPlanning(ps, rs, ts, grbModel, model, planning);

            // Dispose of model and environment
            grbModel.dispose();
            env.dispose();

            return planning;

        } catch (GRBException e) {
            e.printStackTrace();
        } finally {
            // set this flag so other processes know that the solver is finished
            solverContext.setSolving(false);
            watch.stop();
            log.info("Duration of \"Gurobi\" Solver: {}ms", watch.getTime());
        }
        return null;
    }

    private void fillPlanning(List<P> ps, List<R> rs, List<T> ts, GRBModel grbModel, ILPModel model, Planning planning) throws GRBException {
        double[][][] x = grbModel.get(GRB.DoubleAttr.X, model.getX());
        for (int p = 0; p < ps.size(); p++) {
            for (int t = 0; t < ts.size(); t++) {
                for (int r = 0; r < rs.size(); r++) {
                    if (Math.round(x[p][t][r]) == 1.0) {
                        planning.getSolutions().add(new Solution(rs.get(r), ts.get(t), ps.get(p), ps.get(p).getExpert(), ps.get(p).getCoach()));
                    }
                }
            }
        }
    }


}
