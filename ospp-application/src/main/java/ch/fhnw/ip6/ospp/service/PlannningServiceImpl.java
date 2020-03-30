package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.api.SolverApi;
import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.Timeslot;
import ch.fhnw.ip6.ospp.mapper.LecturerMapper;
import ch.fhnw.ip6.ospp.mapper.PresentationMapper;
import ch.fhnw.ip6.ospp.mapper.RoomMapper;
import ch.fhnw.ip6.ospp.mapper.TimeslotMapper;
import ch.fhnw.ip6.ospp.model.Plan;
import ch.fhnw.ip6.ospp.service.client.LecturerService;
import ch.fhnw.ip6.ospp.service.client.PlanningService;
import ch.fhnw.ip6.ospp.service.client.PresentationService;
import ch.fhnw.ip6.ospp.service.client.RoomService;
import ch.fhnw.ip6.ospp.service.client.TimeslotService;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
import ch.fhnw.ip6.ospp.vo.PlanningVO;
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import ch.fhnw.ip6.ospp.vo.RoomVO;
import ch.fhnw.ip6.ospp.vo.TimeslotVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlannningServiceImpl implements PlanningService {

    private final PresentationService presentationService;
    private final LecturerService lecturerService;
    private final RoomService roomService;
    private final TimeslotService timeslotService;

    private final PresentationMapper presentationMapper;
    private final LecturerMapper lecturerMapper;
    private final RoomMapper roomMapper;
    private final TimeslotMapper timeslotMapper;

    private final ApplicationContext applicationContext;

    @Value("${ospp.solver}")
    private String solverName;

    @Value("${ospp.testmode}")
    private boolean testmode;

    @Override
    public Planning plan() {
        List<PresentationVO> presentationVOs = presentationService.getAll();
        List<LecturerVO> lecturerVOs = lecturerService.getAll();
        List<RoomVO> roomVOs = roomService.getAll();
        List<TimeslotVO> timeslotVOs = timeslotService.getAll();

        List<Presentation> presentations = presentationVOs.stream().map(presentationMapper::toDto).collect(Collectors.toList());
        List<Lecturer> lecturers = lecturerVOs.stream().map(lecturerMapper::toDto).collect(Collectors.toList());
        List<Room> rooms = roomVOs.stream().map(roomMapper::toDto).collect(Collectors.toList());
        List<Timeslot> timeslots = timeslotVOs.stream().map(timeslotMapper::toDto).collect(Collectors.toList());

        Planning solution = null;
        if (testmode) {
            getSolver().testSolve();
        } else {
            solution = getSolver().solve(presentations, lecturers, rooms, timeslots);
        }
        return solution;
    }

    @Override
    public PlanningVO getPlanById(long id) {
        return null;
    }

    @Override
    public List<PlanningVO> getAllPlannings() {
        return null;
    }

    private SolverApi getSolver() {
        return (SolverApi) applicationContext.getBean(solverName);
    }
}
