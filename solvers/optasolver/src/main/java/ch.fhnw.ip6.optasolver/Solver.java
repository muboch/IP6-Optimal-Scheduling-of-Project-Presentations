package ch.fhnw.ip6.optasolver;

import ch.fhnw.ip6.api.AbstractSolver;
import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import org.drools.model.Model;
import org.drools.model.impl.ModelImpl;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("ch.fhnw.ip6.optasolver.Solver")
public class Solver extends AbstractSolver {


    public Solver(SolverContext solverContext) {
        super(solverContext);
    }

    @Override
    public Planning solve(List<P> presentations, List<L> lecturers, List<R> rooms, List<T> timeslots, boolean[][] offTimes) {
        solverContext.setSolving(true);

        Model model = new ModelImpl();
        

        return null;
    }


}
