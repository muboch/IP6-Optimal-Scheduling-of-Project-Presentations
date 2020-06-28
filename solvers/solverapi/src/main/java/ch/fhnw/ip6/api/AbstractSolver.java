package ch.fhnw.ip6.api;


import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.common.dto.RoomDto;
import ch.fhnw.ip6.common.dto.TimeslotDto;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.common.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractSolver implements SolverApi {

    @Value("${ospp.timeLimit}")
    public final int timeLimit = 60;

    protected final SolverContext solverContext;

    public Planning testSolve() {
        JsonUtil util = new JsonUtil();
        List<PresentationDto> presentations = new ArrayList<>(util.getJsonAsList("presentations.json", PresentationDto.class));
        List<LecturerDto> lecturers = util.getJsonAsList("lecturers.json", LecturerDto.class);
        List<RoomDto> rooms = util.getJsonAsList("rooms.json", RoomDto.class).stream().filter(r -> r.getReserve().equals(false)).collect(Collectors.toList());
        List<TimeslotDto> timeslots = util.getJsonAsList("timeslots.json", TimeslotDto.class);
        return testSolve(presentations, lecturers, rooms, timeslots);
    }

    public Planning testSolveLarge() {
        JsonUtil util = new JsonUtil();
        List<PresentationDto> presentations = new ArrayList<>(util.getJsonAsList("presentations300.json", PresentationDto.class));
        List<LecturerDto> lecturers = util.getJsonAsList("lecturers300.json", LecturerDto.class);
        List<RoomDto> rooms = util.getJsonAsList("rooms300.json", RoomDto.class).stream().filter(r -> r.getReserve().equals(false)).collect(Collectors.toList());
        List<TimeslotDto> timeslots = util.getJsonAsList("timeslots300.json", TimeslotDto.class);
        return testSolve(presentations, lecturers, rooms, timeslots);
    }

    private Planning testSolve(List<PresentationDto> presentations, List<LecturerDto> lecturers, List<RoomDto> rooms, List<TimeslotDto> timeslots) {
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

        log.info("Solver completed. Best solution:");
        log.info("Planning Stats:\n{}", solverContext.getPlanning().getPlanningStats());
        log.info("Planning:\n{}", solverContext.getPlanning().getPlanningAsTable());
        return solverContext.getPlanning();
    }

}
