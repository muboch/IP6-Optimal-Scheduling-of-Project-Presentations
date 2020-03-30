package ch.fhnw.ip6.api;

import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.Timeslot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
     * @return Solution
     */
    Planning solve(List<Presentation> presentations, List<Lecturer> lecturers, List<Room> rooms, List<Timeslot> timeslots);


    /**
     * Builds the model and calls the solver and returns a solution. Input are test data json files.
     *
     * @return
     */
    Planning testSolve();



}
