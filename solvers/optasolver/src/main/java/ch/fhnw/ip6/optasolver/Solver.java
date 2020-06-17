package ch.fhnw.ip6.optasolver;

import ch.fhnw.ip6.api.AbstractSolver;
import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.StatusEnum;
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
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private final static Logger log = LogManager.getLogger(Solver.class);

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
        StopWatch watch = new StopWatch();
        watch.start();
        log.info("Number of Problem Instances: Presentations: {}, Lecturers: {}, Rooms: {}, Timeslots: {}, OffTimes: {}", ps.size(), ls.size(), rs.size(), ts.size(), offTimes.length);

        List<Presentation> presentations = ps.stream().map(p -> (Presentation) p).collect(Collectors.toList());
        List<Lecturer> lecturers = ls.stream().map(l -> (Lecturer) l).collect(Collectors.toList());
        List<Timeslot> timeslots = ts.stream().map(t -> (Timeslot) t).collect(Collectors.toList());
        List<Room> rooms = rs.stream().map(r -> (Room) r).collect(Collectors.toList());

        // offtimes[lecturers][timeslots]. Map offtimes to timeslots
        for (int i = 0; i < offTimes.length; i++) {
            for (int j = 0; j < offTimes[i].length; j++) {
                if (offTimes[i][j]) {
                    int finalI = i;
                    int finalJ = j;
                    Timeslot timeslot = timeslots.stream().filter(t -> t.getId() == finalJ).findFirst().get();
                    lecturers.stream().filter(l -> l.getId() == finalI).findFirst().get().getOfftimes().add(timeslot);
                }
            }
        }


        lecturers.forEach(l -> l.setPresentations(presentations.stream().filter(p -> p.getExpert().getId() == l.getId() || p.getCoach().getId() == l.getId()).collect(Collectors.toList()))); // map presentations to lecturerDto

        OptaSolution problem = new OptaSolution(timeslots, rooms, presentations, lecturers);
        log.debug("Setup Constraints duration: {}ms", watch.getTime());

        UUID problemId = UUID.randomUUID();
        // Submit the problem to start solving
        log.debug("Start with Optaplanner Optimization");

        SolverJob<OptaSolution, UUID> solverJob = solverManager.solve(problemId, problem);
        OptaSolution solution;

        try {
            // Wait until the solving ends
            solution = solverJob.getFinalBestSolution();
            //solverManager.solveAndListen(problemId, problem);
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Solving failed.", e);
        }
        log.debug("End of Optaplanner Optimization after {}ms", watch.getTime());


        Planning planning = new Planning();
        if (solution != null)
            planning.setStatus(StatusEnum.SOLUTION);
        else {
            planning.setStatus(StatusEnum.NO_SOLUTION);
            return planning;
        }

        Set<Solution> sol = solution.getPresentations().stream().map(p -> new Solution(p.getRoom(), p.getTimeslot(), p, p.getCoach(), p.getExpert())).collect(Collectors.toSet());

        planning.setSolutions(sol);
        planning.setRooms(solution.getRoomList());
        planning.setTimeslots(solution.getTimeslots());

        solutionChecker.generateStats(planning, lecturers, presentations, timeslots, rooms);
        planning.setCost(solutionChecker.getTotalPlanningCost());
        log.info("New Planning Nr. {} - Cost: {}\n{}\n{}", planning.getNr(), planning.getCost(), planning.getPlanningStats(), planning.getPlanningAsTable());
        watch.stop();
        log.info("Duration of Optasolver: {}ms", watch.getTime());

        solverContext.setSolving(false);
        return planning;
    }


}
