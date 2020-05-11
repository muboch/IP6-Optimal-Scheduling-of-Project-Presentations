package ch.fhnw.ip6.ilpsolver;

import ch.fhnw.ip6.api.SolverApi;
import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.common.dto.RoomDto;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.TimeslotDto;
import ch.fhnw.ip6.common.util.JsonUtil;
import ch.fhnw.ip6.ilpsolver.callback.ILPSolverCallback;
import ch.fhnw.ip6.ilpsolver.constraint.Constraint;
import ch.fhnw.ip6.ilpsolver.constraint.SoftConstraint;
import ch.fhnw.ip6.ilpsolver.constraint.hard.AllPresentationsToRoomAndTimeslotAssigned;
import ch.fhnw.ip6.ilpsolver.constraint.hard.LecturerNotMoreThanOnePresentationPerTimeslot;
import ch.fhnw.ip6.ilpsolver.constraint.hard.OnlyOnePresentationPerRoomAndTimeslot;
import ch.fhnw.ip6.ilpsolver.constraint.soft.MinRoomUsages;
import ch.fhnw.ip6.ilpsolver.constraint.soft.MinTimeslotUsages;
import ch.fhnw.ip6.solutionchecker.SolutionCheckerCarlo;
import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Solver implements SolverApi {


    @Override
    public Planning testSolve() {
        JsonUtil util = new JsonUtil();

        List<PresentationDto> presentations = util.getJsonAsList("presentations.json", PresentationDto.class);
        List<LecturerDto> lecturers = util.getJsonAsList("lecturers.json", LecturerDto.class);
        List<RoomDto> rooms = util.getJsonAsList("rooms.json", RoomDto.class).stream().filter(r -> r.getReserve().equals(false)).collect(Collectors.toList());
        List<TimeslotDto> timeslots = util.getJsonAsList("timeslots.json", TimeslotDto.class);

        for (PresentationDto p : presentations) {
            p.setCoach(lecturers.stream().filter(t -> t.getInitials().equals(p.getCoachInitials())).findFirst().get()); // Assign Coaches to Presentation
            p.setExpert(lecturers.stream().filter(t -> t.getInitials().equals(p.getExpertInitials())).findFirst().get()); // Assign Experts to Presentation
        }

        return solve(presentations, lecturers, rooms, timeslots, new boolean[0][0]);
    }

    @Override
    public Planning solve(List<PresentationDto> ps, List<LecturerDto> ls, List<RoomDto> rs, List<TimeslotDto> ts, boolean[][] locktimes) {

        try {

            GRBEnv env = new GRBEnv();
            GRBModel grbModel = new GRBModel(env);
            grbModel.set(GRB.StringAttr.ModelName, "ospp-fms");

            ILPModel model = new ILPModel(ps, ls, rs, ts, locktimes, grbModel);

            GRBLinExpr objective = new GRBLinExpr();

            List<Constraint> constraints = new ArrayList<>();
            constraints.add(new AllPresentationsToRoomAndTimeslotAssigned());
            constraints.add(new LecturerNotMoreThanOnePresentationPerTimeslot());
            constraints.add(new OnlyOnePresentationPerRoomAndTimeslot());
            constraints.add(new MinTimeslotUsages());
            constraints.add(new MinRoomUsages());
            constraints.forEach(c -> {
                if (c instanceof SoftConstraint) {
                    ((SoftConstraint) c).setObjectives(objective);
                }
                c.setModel(model).build();
            });

            grbModel.setCallback(new ILPSolverCallback(model));
            grbModel.setObjective(objective);
            grbModel.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);
            grbModel.update();
            grbModel.optimize();

            System.out.println("#########################################   Solution   ###########################################");

            Planning planning = new Planning();
            planning.setTimeslots(ts);
            planning.setRooms(rs);
            printSolution(ps, rs, ts, grbModel, model, planning);
            SolutionCheckerCarlo solutionChecker = new SolutionCheckerCarlo(planning, ls, ps, ts, rs);
            solutionChecker.check();
            planning.setCost(solutionChecker.getCost());
            System.out.println(planning.toString());
            System.out.println("################################################################################################");

            // Dispose of model and environment
            grbModel.dispose();
            env.dispose();

        } catch (GRBException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void printSolution(List<PresentationDto> ps, List<RoomDto> rs, List<TimeslotDto> ts, GRBModel grbModel, ILPModel model, Planning planning) throws GRBException {
        double[][][] xd = grbModel.get(GRB.DoubleAttr.X, model.getX());
        for (int p = 0; p < ps.size(); p++) {
            for (int t = 0; t < ts.size(); t++) {
                for (int r = 0; r < rs.size(); r++) {
                    if (xd[p][t][r] != 0.0) {
                        planning.getSolutions().add(new Solution(rs.get(r), ts.get(t), ps.get(p), ps.get(p).getExpert(), ps.get(p).getCoach()));
                    }
                }
            }
        }
    }


}
