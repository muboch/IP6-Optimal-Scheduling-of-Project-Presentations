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
import ch.fhnw.ip6.solutionchecker.SolutionChecker;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("ch.fhnw.ip6.ilpsolver.Solver")
public class Solver extends AbstractSolver {

    public Solver(SolverContext solverContext) {
        super(solverContext);
    }

    @Override
    public Planning solve(List<P> ps, List<L> ls, List<R> rs, List<T> ts, boolean[][] offTimes) {

        try {

            GRBEnv env = new GRBEnv();
            GRBModel grbModel = new GRBModel(env);
            grbModel.set(GRB.StringAttr.ModelName, "ospp-fms");

            ILPModel model = new ILPModel(ps, ls, rs, ts, offTimes, grbModel);

            GRBLinExpr objective = new GRBLinExpr();

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

            grbModel.setCallback(new ILPSolverCallback(model));
            grbModel.setObjective(objective);
            grbModel.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
            grbModel.set(GRB.IntParam.Method, 2);
          // grbModel.set(GRB.IntParam.Seed, (int) (Math.random() * 100000));
            grbModel.update();
            grbModel.optimize();

            Planning planning = new Planning();
            planning.setTimeslots(ts);
            planning.setRooms(rs);
            fillPlanning(ps, rs, ts, grbModel, model, planning);
            SolutionChecker solutionChecker = new SolutionChecker();
            solutionChecker.generateStats(planning, ls, ps, ts, rs);
            System.out.println(planning.getPlanningStats());

//            GRBVar[] vars = grbModel.getVars();
//            System.out.println("Print out the currentRoomNotPrevRoom Vars that are true (>0.5):");
//            TreeMap<String, Double> myVars = new TreeMap<>();
//            for (GRBVar var : vars) {
//                if (var.get(GRB.StringAttr.VarName).startsWith("currentRoomNotPrevRoom") && var.get(GRB.DoubleAttr.X) > 0.5) {
//                    myVars.put(var.get(GRB.StringAttr.VarName),var.get(GRB.DoubleAttr.X));
//                }
//            }
//            myVars.forEach((key1, value1) -> System.out.println(key1 + " -> " + value1));

            grbModel.write("model.mst");
            grbModel.write("out.sol");

            // Dispose of model and environment
            grbModel.dispose();
            env.dispose();

        } catch (GRBException e) {
            e.printStackTrace();
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
