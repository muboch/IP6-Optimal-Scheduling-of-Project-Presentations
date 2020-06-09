package ch.fhnw.ip6.optasolver;

import ch.fhnw.ip6.api.AbstractSolver;
import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.*;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.solutionchecker.SolutionChecker;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component("ch.fhnw.ip6.optasolver.Solver")
public class Solver extends AbstractSolver {

    private final SolutionChecker solutionChecker;
    @Autowired
    private SolverManager<OptaSolution, UUID> solverManager;

    public Solver(SolverContext solverContext) {
        super(solverContext);
        SolverConfig solverConfig = SolverConfig.createFromXmlResource("solverconfig.xml");
        SolverManager<OptaSolution, UUID> solverManager = SolverManager.create(solverConfig, new SolverManagerConfig());
        //SolverManager<OptaSolution, UUID> solverManager = SolverManager.create(new SolverConfig(), new SolverManagerConfig());
        this.solverManager = solverManager;
        this.solutionChecker = new SolutionChecker();


    }

    @Override
    public Planning solve(List<P> ps, List<L> ls, List<R> rs, List<T> ts, boolean[][] offTimes) {
        solverContext.setSolving(true);

        List<PresentationDto> presentations = ps.stream().map(p -> (PresentationDto) p).collect(Collectors.toList());
        List<LecturerDto> lecturers = ls.stream().map(p -> (LecturerDto) p).collect(Collectors.toList());
        List<TimeslotDto> timeslots = ts.stream().map(p -> (TimeslotDto) p).collect(Collectors.toList());
        List<RoomDto> rooms = rs.stream().map(p -> (RoomDto) p).collect(Collectors.toList());

        OptaSolution problem = new OptaSolution(timeslots, rooms, presentations, lecturers);

        UUID problemId = UUID.randomUUID();
        // Submit the problem to start solving
        SolverJob<OptaSolution, UUID> solverJob = solverManager.solve(problemId, problem);
        OptaSolution solution;
        try {
            // Wait until the solving ends
            solution = solverJob.getFinalBestSolution();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Solving failed.", e);
        }
        Set<Solution> sol = solution.getPresentationList().stream().map(p -> {
            return new Solution(p.getRoom(), p.getTimeslot(), p, p.getCoach(), p.getExpert());
        }).collect(Collectors.toSet());
        Planning p = new Planning();


        p.setSolutions(sol);
        p.setRooms(solution.getRoomList());
        p.setTimeslots(solution.getTimeslotList());

        solutionChecker.generateStats(p, ls, ps, ts, rs);
        p.setCost(solutionChecker.getTotalPlanningCost());

        System.out.println(p.getPlanningStats());
        System.out.println(p.getPlanningAsTable());
        return p;
/*
        Model model = new ModelImpl();
        return null;

 */
    }


}
