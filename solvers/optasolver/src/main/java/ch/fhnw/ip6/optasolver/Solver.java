package ch.fhnw.ip6.optasolver;

import ch.fhnw.ip6.api.AbstractSolver;
import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.common.util.JsonUtil;
import ch.fhnw.ip6.optasolver.model.Lecturer;
import ch.fhnw.ip6.optasolver.model.Presentation;
import ch.fhnw.ip6.optasolver.model.Room;
import ch.fhnw.ip6.optasolver.model.Timeslot;
import ch.fhnw.ip6.solutionchecker.SolutionChecker;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component("ch.fhnw.ip6.optasolver.Solver")
public class Solver extends AbstractSolver {

    private final SolutionChecker solutionChecker;

    private final SolverManager<OptaSolution, UUID> solverManager;

    public Solver(SolverContext solverContext) {
        super(solverContext);
        SolverConfig solverConfig = SolverConfig.createFromXmlResource("solverconfig.xml");
        this.solverManager = SolverManager.create(solverConfig, new SolverManagerConfig());
        this.solutionChecker = new SolutionChecker();
    }

    @Override
    public Planning testSolve() {

        JsonUtil util = new JsonUtil();

        List<Presentation> presentations = new ArrayList<>(util.getJsonAsList("presentations.json", Presentation.class));//.subList(0, 10);
        List<Lecturer> lecturers = util.getJsonAsList("lecturers.json", Lecturer.class);
        List<Room> rooms = util.getJsonAsList("rooms.json", Room.class).stream().filter(r -> r.getReserve().equals(false)).collect(Collectors.toList());
        List<Timeslot> timeslots = util.getJsonAsList("timeslots.json", Timeslot.class);

        for (PresentationDto p : presentations) {
            p.setCoach(lecturers.stream().filter(t -> t.getInitials().equals(p.getCoachInitials())).findFirst().get()); // Assign Coaches to Presentation
            p.setExpert(lecturers.stream().filter(t -> t.getInitials().equals(p.getExpertInitials())).findFirst().get()); // Assign Experts to Presentation
        }

        solve(
                presentations.stream().map(x -> (P) x).collect(Collectors.toList()),
                lecturers.stream().map(x -> (L) x).collect(Collectors.toList()),
                rooms.stream().map(x -> (R) x).collect(Collectors.toList()),
                timeslots.stream().map(x -> (T) x).collect(Collectors.toList()),
                new boolean[lecturers.size()][timeslots.size()]
        );
        return solverContext.getPlanning();
    }

    @Override
    public Planning solve(List<P> ps, List<L> ls, List<R> rs, List<T> ts, boolean[][] offTimes) {
        solverContext.setSolving(true);

        List<Presentation> presentations = ps.stream().map(p -> (Presentation) p).collect(Collectors.toList());
        List<Lecturer> lecturers = ls.stream().map(l -> (Lecturer) l).collect(Collectors.toList());
        List<Timeslot> timeslots = ts.stream().map(t -> (Timeslot) t).collect(Collectors.toList());
        List<Room> rooms = rs.stream().map(r -> (Room) r).collect(Collectors.toList());

        lecturers.forEach(l -> l.setPresentations(presentations.stream().filter(p -> p.getExpert().getId() == l.getId() || p.getCoach().getId() == l.getId()).collect(Collectors.toList()))); // map presentations to lecturerDto

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
        Set<Solution> sol = solution.getPresentations().stream().map(p -> new Solution(p.getRoom(), p.getTimeslot(), p, p.getCoach(), p.getExpert())).collect(Collectors.toSet());

        Planning p = new Planning();

        p.setSolutions(sol);
        p.setRooms(solution.getRoomList());
        p.setTimeslots(solution.getTimeslots());

        solutionChecker.generateStats(p, lecturers, presentations, timeslots, rooms);
        p.setCost(solutionChecker.getTotalPlanningCost());

        System.out.println(p.getPlanningStats());
        System.out.println(p.getPlanningAsTable());
        return p;

    }
}
