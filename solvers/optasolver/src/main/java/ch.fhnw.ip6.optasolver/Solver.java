package ch.fhnw.ip6.optasolver;

import ch.fhnw.ip6.api.AbstractSolver;
import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.common.dto.RoomDto;
import ch.fhnw.ip6.common.dto.TimeslotDto;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component("ch.fhnw.ip6.optasolver.Solver")
public class Solver extends AbstractSolver {

    @Autowired
    private SolverManager<OptaSolution, UUID> solverManager;

    public Solver(SolverContext solverContext) {
        super(solverContext);
        SolverConfig solverConfig = SolverConfig.createFromXmlResource("solverconfig.xml");
        SolverManager<OptaSolution, UUID> solverManager = SolverManager.create(solverConfig, new SolverManagerConfig());
        //SolverManager<OptaSolution, UUID> solverManager = SolverManager.create(new SolverConfig(), new SolverManagerConfig());
        this.solverManager = solverManager;


    }

    @Override
    public Planning solve(List<P> ps, List<L> ls, List<R> rs, List<T> ts, boolean[][] offTimes) {
        solverContext.setSolving(true);

        List<PresentationDto> presentations = ps.stream().map(p -> (PresentationDto) p).collect(Collectors.toList());
        List<LecturerDto> lecturers = ps.stream().map(p -> (LecturerDto) p).collect(Collectors.toList());
        List<TimeslotDto> timeslots = ps.stream().map(p -> (TimeslotDto) p).collect(Collectors.toList());
        List<RoomDto> rooms = ps.stream().map(p -> (RoomDto) p).collect(Collectors.toList());

        OptaSolution problem = new OptaSolution(timeslots, rooms, presentations);

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

        Planning p = new Planning();
        p.setRooms(solution.getRoomList());
        p.setTimeslots(solution.getTimeslotList());
        System.out.println(solution.toString()
        );

        return p;




/*
        Model model = new ModelImpl();
        return null;

 */
    }


}
