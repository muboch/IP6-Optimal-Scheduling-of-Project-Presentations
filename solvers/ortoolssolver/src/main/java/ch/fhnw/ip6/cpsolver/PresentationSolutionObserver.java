package ch.fhnw.ip6.cpsolver;

import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.solutionchecker.SolutionChecker;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.IntVar;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PresentationSolutionObserver extends CpSolverSolutionCallback {
    private final IntVar[][][] presRoomTime;
    private final List<P> presentations;
    private final List<L> lecturers;
    private final List<T> timeslots;
    private final List<R> rooms;
    private final StopWatch stopWatch;
    private final SolverContext solverContext;
    private final SolutionChecker solutionChecker;
    private int solutionCount;
    private final Logger log = LoggerFactory.getLogger(PresentationSolutionObserver.class);


    @Override
    public void onSolutionCallback() {
        solutionCount++;
        System.out.println("Solution " + solutionCount + " . Time: " + stopWatch.getTime());
        Planning planning = new Planning();
        planning.setNr(solutionCount);
        planning.setTimeslots(timeslots);
        planning.setRooms(rooms);

        for (T t : timeslots) {
            for (R r : rooms) {
                for (P p : presentations) {
                    if (presRoomTime[presentations.indexOf(p)][rooms.indexOf(r)][timeslots.indexOf(t)] == null)
                        continue;
                    if (booleanValue(presRoomTime[presentations.indexOf(p)][rooms.indexOf(r)][timeslots.indexOf(t)])) {
                        planning.getSolutions().add(new Solution(r, t, p, p.getExpert(), p.getCoach()));
                    }
                }
            }
        }
        solutionChecker.generateStats(planning, lecturers, presentations, timeslots, rooms);
        planning.setCost(solutionChecker.getTotalPlanningCost());
        System.out.println("Solver reported cost:" + objectiveValue() + " Branches:" + numBranches() + " Conflicts:" + numConflicts());
        solverContext.saveBestPlanning(planning);
        log.info("New Planning Nr. {} - Cost: {}\n{}", planning.getNr(), planning.getCost(), planning.getPlanningAsTable());

    }

    public PresentationSolutionObserver(IntVar[][][] presRoomTime, List<L> lecturers, List<P> presentations, List<T> timeslots, List<R> rooms, StopWatch stopWatch, SolverContext solverContext) {
        this.presRoomTime = presRoomTime;
        this.lecturers = lecturers;
        this.presentations = presentations;
        this.timeslots = timeslots;
        this.rooms = rooms;
        this.solutionCount = 0;
        this.stopWatch = stopWatch;
        this.solverContext = solverContext;
        this.solutionChecker = new SolutionChecker();
    }
}
