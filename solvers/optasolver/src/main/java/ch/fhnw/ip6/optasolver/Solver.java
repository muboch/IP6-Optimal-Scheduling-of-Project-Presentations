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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
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
    public Planning testSolveLarge() {
        JsonUtil util = new JsonUtil();
        List<Presentation> presentations = new ArrayList<>(util.getJsonAsList("presentations300.json", Presentation.class));
        List<Lecturer> lecturers = util.getJsonAsList("lecturers.json", Lecturer.class);
        List<Room> rooms = util.getJsonAsList("rooms.json", Room.class).stream().filter(r -> r.getReserve().equals(false)).collect(Collectors.toList());
        List<Timeslot> timeslots = util.getJsonAsList("timeslots.json", Timeslot.class);
        return testSolve(presentations, lecturers, rooms, timeslots);
    }

    @Override
    public Planning testSolve() {
        JsonUtil util = new JsonUtil();
        List<Presentation> presentations = new ArrayList<>(util.getJsonAsList("presentations.json", Presentation.class));
        List<Lecturer> lecturers = util.getJsonAsList("lecturers.json", Lecturer.class);
        List<Room> rooms = util.getJsonAsList("rooms.json", Room.class).stream().filter(r -> r.getReserve().equals(false)).collect(Collectors.toList());
        List<Timeslot> timeslots = util.getJsonAsList("timeslots.json", Timeslot.class);
        return testSolve(presentations, lecturers, rooms, timeslots);
    }


    private Planning testSolve(List<Presentation> presentations, List<Lecturer> lecturers, List<Room> rooms, List<Timeslot> timeslots) {

        timeslots
                .stream()
                .sorted(Comparator.comparingInt(Timeslot::getId))
                .forEach(timeslot -> timeslot.setSortOrder(timeslot.getId()));


        mapCoachesAndExperts(presentations, lecturers);

        solve(
                presentations.stream().map(x -> (P) x).collect(Collectors.toList()),
                lecturers.stream().map(x -> (L) x).collect(Collectors.toList()),
                rooms.stream().map(x -> (R) x).collect(Collectors.toList()),
                timeslots.stream().map(x -> (T) x).collect(Collectors.toList()),
                new boolean[lecturers.size()][timeslots.size()]
        );
        return solverContext.getPlanning();
    }

    protected void mapCoachesAndExperts(List<Presentation> presentations, List<Lecturer> lecturers) {
        for (PresentationDto p : presentations) {
            p.setCoach(lecturers.stream().filter(t -> t.getInitials().equals(p.getCoachInitials())).findFirst().get()); // Assign Coaches to Presentation
            p.setExpert(lecturers.stream().filter(t -> t.getInitials().equals(p.getExpertInitials())).findFirst().get()); // Assign Experts to Presentation
        }
    }


    @Override
    public Planning solve(List<P> ps, List<L> ls, List<R> rs, List<T> ts, boolean[][] offTimes) {
        try {


            init();
            StopWatch watch = new StopWatch();
            watch.start();
            log.info("Start Opta-Solver");
            log.info("Timelimit: " + timeLimit);
            log.info("Number of Problem Instances: Presentations: " + ps.size()
                    + ", Lecturers: " + ls.size()
                    + ", Rooms: " + rs.size()
                    + ", Timeslots: "
                    + ts.size()
                    + ", OffTimes: " + offTimes.length);

            List<Presentation> presentations = ps.stream().map(p -> (Presentation) p).collect(Collectors.toList());
            List<Lecturer> lecturers = ls.stream().map(l -> (Lecturer) l).collect(Collectors.toList());
            List<Timeslot> timeslots = ts.stream().map(t -> (Timeslot) t).collect(Collectors.toList());
            List<Room> rooms = rs.stream().map(r -> (Room) r).collect(Collectors.toList());

            // offtimes[lecturers][timeslots]. Map offtimes to timeslots

                for (int i = 0; i < lecturers.size(); i++) {
                    for (int j = 0; j < timeslots.size(); j++) {
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
            log.info("Setup Constraints duration: " + watch.getTime() + "ms");

            UUID problemId = UUID.randomUUID();
            // Submit the problem to start solving
            log.info("Start with Optimization");

            SolverConfig solverConfig = SolverConfig.createFromXmlResource("solverconfig.xml");
            solverConfig.withTerminationConfig(new TerminationConfig().withSecondsSpentLimit((long) timeLimit));

            SolverJob<OptaSolution, UUID> solverJob = solverManager.solve(problemId, problem);
            OptaSolution solution;

            // Wait until the solving ends
            solution = solverJob.getFinalBestSolution();
            log.info("End of Optaplanner Optimization after " + watch.getTime() + "ms");


            Planning planning = new Planning();
            if (solution != null)
                planning.setStatus(StatusEnum.SOLUTION);
            else {
                planning.setStatus(StatusEnum.NO_SOLUTION);
                solverContext.setPlanning(planning);
                return planning;
            }

            Set<Solution> sol = solution.getPresentations().stream().map(p -> new Solution(p.getRoom(), p.getTimeslot(), p, p.getCoach(), p.getExpert())).collect(Collectors.toSet());

            planning.setSolutions(sol);
            planning.setRooms(solution.getRoomList());
            planning.setTimeslots(solution.getTimeslots());

            solutionChecker.generateStats(planning, lecturers, presentations, timeslots, rooms);
            planning.setCost(solutionChecker.getTotalPlanningCost());
            log.info("New Planning Nr. " + planning.getNr() + " - Cost: " + planning.getCost() + "\n" + planning.getPlanningStats() + "\n" + planning.getPlanningAsTable());
            watch.stop();
            log.info("Duration of Optasolver: " + watch.getTime() + "ms");
            solverContext.setPlanning(planning);
            return planning;
        } catch (InterruptedException | ExecutionException e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            reset();
        }
        Planning p = new Planning();
        p.setStatus(StatusEnum.NO_SOLUTION);
        solverContext.setPlanning(p);
        return p;
    }

}
