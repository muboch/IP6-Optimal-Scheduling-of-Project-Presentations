package ch.fhnw.ip6.api;

import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SolverApi {


    /**
     * Builds the model and calls the solver and returns a solution.
     *
     * @param presentations {@link List} of {@link P}
     * @param lecturers     {@link List} of {@link L}
     * @param rooms         {@link List} of {@link R}
     * @param timeslots     {@link List} of {@link T}
     * @param offTimes
     * @return Solution
     */
    Planning solve(List<P> presentations, List<L> lecturers, List<R> rooms, List<T> timeslots, boolean[][] offTimes);


    /**
     * Builds the model and calls the solver and returns a solution. Input are test data json files.
     *
     * @return
     */
    Planning testSolve();

    /**
     * Builds the model and calls the solver and returns a solution. Input are test data json files with 300 presentations.
     *
     * @return
     */
    Planning testSolveLarge();

}
