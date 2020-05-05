package ch.fhnw.ip6.api;

import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.common.dto.RoomDto;
import ch.fhnw.ip6.common.dto.TimeslotDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface SolverApi {



    /**
     * Builds the model and calls the solver and returns a solution.
     *
     * @param presentations {@link List} of {@link PresentationDto}
     * @param lecturers     {@link List} of {@link LecturerDto}
     * @param rooms         {@link List} of {@link RoomDto}
     * @param timeslots     {@link List} of {@link TimeslotDto}
     * @param locktimes
     * @return Solution
     */
    Planning solve(List<PresentationDto> presentations, List<LecturerDto> lecturers, List<RoomDto> rooms, List<TimeslotDto> timeslots, boolean[][] locktimes);


    /**
     * Builds the model and calls the solver and returns a solution. Input are test data json files.
     *
     * @return
     */
    Planning testSolve();



}
