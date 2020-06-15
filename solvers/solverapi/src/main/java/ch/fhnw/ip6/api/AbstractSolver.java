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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class AbstractSolver implements SolverApi {

    @Value("${ospp.timeLimit}")
    public final int timeLimit = 3600;

    protected final SolverContext solverContext;

    private final static Logger log = LoggerFactory.getLogger(AbstractSolver.class);


    public Planning testSolve() {
        JsonUtil util = new JsonUtil();
        List<PresentationDto> presentations = new ArrayList<>(util.getJsonAsList("presentations.json", PresentationDto.class));//.subList(0, 3);
        List<LecturerDto> lecturers = util.getJsonAsList("lecturers.json", LecturerDto.class);
        List<RoomDto> rooms = util.getJsonAsList("rooms.json", RoomDto.class).stream().filter(r -> r.getReserve().equals(false)).collect(Collectors.toList());
        List<TimeslotDto> timeslots = util.getJsonAsList("timeslots.json", TimeslotDto.class);

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
