package ch.fhnw.ip6.api;

import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Timeslot;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SolverApi {



    /**
     * Builds the model and calls the solver and returns a solution.
     *
     * @param presentations {@link List} of {@link Presentation}
     * @param lecturers     {@link List} of {@link Lecturer}
     * @param rooms         {@link List} of {@link Room}
     * @param timeslots     {@link List} of {@link Timeslot}
     * @param locktimes
     * @return Solution
     */
    Planning solve(List<Presentation> presentations, List<Lecturer> lecturers, List<Room> rooms, List<Timeslot> timeslots, boolean[][] locktimes);


    /**
     * Builds the model and calls the solver and returns a solution. Input are test data json files.
     *
     * @return
     */
    Planning testSolve();



}
