package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.api.SolverApi;
import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.*;
import ch.fhnw.ip6.ospp.event.SolveEvent;
import ch.fhnw.ip6.ospp.mapper.LecturerMapper;
import ch.fhnw.ip6.ospp.mapper.PresentationMapper;
import ch.fhnw.ip6.ospp.mapper.RoomMapper;
import ch.fhnw.ip6.ospp.mapper.TimeslotMapper;
import ch.fhnw.ip6.ospp.model.CSV;
import ch.fhnw.ip6.ospp.persistence.PlanningRepository;
import ch.fhnw.ip6.ospp.service.client.*;
import ch.fhnw.ip6.ospp.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlannningServiceImpl  extends AbstractService implements PlanningService {

    private final PresentationService presentationService;
    private final LecturerService lecturerService;
    private final RoomService roomService;
    private final TimeslotService timeslotService;

    private final PlanningRepository planningRepository;

    private final PresentationMapper presentationMapper;
    private final LecturerMapper lecturerMapper;
    private final RoomMapper roomMapper;
    private final TimeslotMapper timeslotMapper;

    private final ApplicationContext applicationContext;
    private final SolverContext solverContext;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Value("${ospp.solver}")
    private String solverName;

    @Value("${ospp.testmode}")
    private boolean testmode = true;

    @Override
    public Planning plan() throws Exception {
        List<PresentationVO> presentationVOs = presentationService.getAll();
        List<LecturerVO> lecturerVOs = lecturerService.getAll();
        List<RoomVO> roomVOs = roomService.getAll();
        List<TimeslotVO> timeslotVOs = timeslotService.getAll();

        List<Presentation> presentations = presentationVOs.stream().map(presentationMapper::toDto).collect(Collectors.toList());
        List<Lecturer> lecturers = lecturerVOs.stream().map(lecturerMapper::toDto).collect(Collectors.toList());
        List<Room> rooms = roomVOs.stream().map(roomMapper::toDto).collect(Collectors.toList());
        List<Timeslot> timeslots = timeslotVOs.stream().map(timeslotMapper::toDto).collect(Collectors.toList());

        boolean[][] locktimes = createLocktimesMap(lecturerVOs, timeslotVOs.size());


        Planning planning;
        if (solverContext.isSolving()) {
            throw new Exception("Solver is already running.");
        }
        if (testmode) {
            planning = getSolver().testSolve();
        } else {
            planning = getSolver().solve(presentations, lecturers, rooms, timeslots, locktimes);
        }

        CSV csv = transformToCsv(planning);

        ch.fhnw.ip6.ospp.model.Planning planningEntity = new ch.fhnw.ip6.ospp.model.Planning();
        planningEntity.setNr(String.valueOf(planning.getNr()));
        planningEntity.setData(csv.getContent());
        planningEntity.setName(csv.getName());
        planningRepository.save(planningEntity);

        return planning;
    }

    private boolean[][] createLocktimesMap(List<LecturerVO> lecturerVOs, int numberOfTimeslots) {

        boolean[][] locktimes = new boolean[lecturerVOs.size()][numberOfTimeslots];

        for (int l = 0; l < lecturerVOs.size(); l++) {
            for (int t = 0; t < numberOfTimeslots; t++) {
                int finalT = t;
                locktimes[l][t] = lecturerVOs.get(l).getLocktimes().stream().filter(timeslotVO -> timeslotVO.getExternalId() == finalT).findFirst().isPresent();
            }
        }

        return locktimes;
    }

    private CSV transformToCsv(Planning planning) {

        String fileName = "Planning_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        StringWriter sw = new StringWriter();
//        try {
//            CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.EXCEL.withDelimiter(';').withHeader(
//                    "nr", "title", "name", "schoolclass", "name2", "schoolclass2", "coach", "expert", "timeslot", "room"
//            ));
//
//            planning.getSolutions().forEach(s -> {
//                try {
//                    csvPrinter.printRecord(
//                            s.getPresentation().getNr(),
//                            s.getPresentation().getTitle(),
//                            s.getPresentation().getName(),
//                            s.getPresentation().getSchoolclass(),
//                            s.getPresentation().getName2(),
//                            s.getPresentation().getSchoolclass2(),
//                            s.getCoach().getName(),
//                            s.getExpert().getName(),
//                            s.getTimeSlot().getDate(),
//                            s.getRoom().getName());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//            sw.flush();
//
//            return CSV.builder().content(sw.toString().getBytes(StandardCharsets.UTF_8)).name(fileName).build();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return null;
    }

    @Override
    public void firePlanning() throws Exception {
        if (solverContext.isSolving()) {
            throw new Exception("Solver is already running.");
        }
        applicationEventPublisher.publishEvent(new SolveEvent(this));
    }

    @Override
    public CSV getFileById(long id) {
        ch.fhnw.ip6.ospp.model.Planning planning = planningRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No planning for id " + id));
        return CSV.builder().name(planning.getName()).content(planning.getData()).build();
    }

    @Override
    public List<PlanningVO> getAllPlannings() {
        return planningRepository.findAllProjectedBy();
    }

    private SolverApi getSolver() {
        return (SolverApi) applicationContext.getBean(solverName);
    }


}
