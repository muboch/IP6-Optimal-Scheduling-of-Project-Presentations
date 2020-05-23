package ch.fhnw.ip6.originalsolver;

import ch.fhnw.ip6.api.AbstractSolver;
import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("ch.fhnw.ip6.ilpsolver.Solver")
public class Solver extends AbstractSolver {

    public Solver(SolverContext solverContext) {
        super(solverContext);
    }

    @Override
    public Planning solve(List<P> ps, List<L> ls, List<R> rs, List<T> ts, boolean[][] offTimes) {


        System.out.println("#########################################   Solution   ###########################################");
        Planning planning = new Planning();
        planning.setTimeslots(ts);
        planning.setRooms(rs);
        System.out.println(planning.toString());
        System.out.println("################################################################################################");

        return solverContext.getPlanning();
    }


}
