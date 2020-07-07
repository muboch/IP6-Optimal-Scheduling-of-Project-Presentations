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
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractSolver implements SolverApi {


    @Value("${ospp.timeLimit}")
    public int timeLimit = 120;

    protected final SolverContext solverContext;

    public Planning testSolve() {
        JsonUtil util = new JsonUtil();
        List<PresentationDto> presentations = new ArrayList<>(util.getJsonAsList("presentations.json", PresentationDto.class));
        return testSolve(presentations);
    }

    public Planning testSolveLarge() {
        JsonUtil util = new JsonUtil();
        List<PresentationDto> presentations = new ArrayList<>(util.getJsonAsList("presentations300.json", PresentationDto.class));
        return testSolve(presentations);
    }

    private Planning testSolve(List<PresentationDto> presentations) {
        JsonUtil util = new JsonUtil();
        List<LecturerDto> lecturers = util.getJsonAsList("lecturers.json", LecturerDto.class);
        List<RoomDto> rooms = util.getJsonAsList("rooms.json", RoomDto.class).stream().filter(r -> r.getReserve().equals(false)).collect(Collectors.toList());
        List<TimeslotDto> timeslots = util.getJsonAsList("timeslots.json", TimeslotDto.class);
        timeslots
                .stream()
                .sorted(Comparator.comparingInt(TimeslotDto::getId))
                .forEach(timeslot -> timeslot.setSortOrder(timeslot.getId()));
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

    public void init() {
        solverContext.setIsSolving(true);
        solverContext.setTimeLimit(timeLimit);
        solverContext.setStartTime(LocalDateTime.now());
    }

    public void reset() {
        solverContext.setIsSolving(false);
    }

}
