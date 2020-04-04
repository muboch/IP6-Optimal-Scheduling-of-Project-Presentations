package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.api.SolverApi;
import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Timeslot;
import ch.fhnw.ip6.ospp.mapper.LecturerMapper;
import ch.fhnw.ip6.ospp.mapper.PlanningMapper;
import ch.fhnw.ip6.ospp.mapper.PresentationMapper;
import ch.fhnw.ip6.ospp.mapper.RoomMapper;
import ch.fhnw.ip6.ospp.mapper.TimeslotMapper;
import ch.fhnw.ip6.ospp.persistence.PlanningRepository;
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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
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

    private final PlanningRepository planningRepository;

    private final PresentationMapper presentationMapper;
    private final LecturerMapper lecturerMapper;
    private final RoomMapper roomMapper;
    private final TimeslotMapper timeslotMapper;
    private final PlanningMapper planningMapper;

    private final ApplicationContext applicationContext;

    @Value("${ospp.solver}")
    private String solverName;

    @Value("${ospp.testmode}")
    private boolean testmode = true;

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

        Planning planning = null;
        if (testmode) {
            planning = getSolver().testSolve();
        } else {
            planning = getSolver().solve(presentations, lecturers, rooms, timeslots);
        }

        String fileName = "Planning_" + planning.getNr() + "_" + LocalDate.now().toString();
        byte[] file = transformToCsv(planning, fileName);

        ch.fhnw.ip6.ospp.model.Planning planningEntity = new ch.fhnw.ip6.ospp.model.Planning();
        planningEntity.setNr(String.valueOf(planning.getNr()));
        planningEntity.setPlanning(file);
        planningEntity.setName(fileName);
        planningRepository.save(planningEntity);

        return planning;
    }

    private byte[] transformToCsv(Planning planning, String fileName) {
        StringWriter sw = new StringWriter();
        try {
            CSVPrinter csvPrinter = new CSVPrinter(sw, CSVFormat.EXCEL.withDelimiter(';').withHeader(
                    "nr", "title", "name", "schoolclass", "name2", "schoolclass2", "coach", "expert", "timeslot", "room"
            ));

            planning.getSolutions().forEach(s -> {
                try {
                    csvPrinter.printRecord(
                            s.getPresentation().getNr(),
                            s.getPresentation().getTitle(),
                            s.getPresentation().getName(),
                            s.getPresentation().getSchoolclass(),
                            s.getPresentation().getName2(),
                            s.getPresentation().getSchoolclass2(),
                            s.getCoach().getName(),
                            s.getExpert().getName(),
                            s.getTimeSlot().getDate(),
                            s.getRoom().getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            sw.flush();
            File file = new File(fileName + ".csv");
            return sw.toString().getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public byte[] getFileById(long id) {
        return planningRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No planning for id " + id)).getPlanning();
    }

    @Override
    public List<PlanningVO> getAllPlannings() {
        return planningRepository.findAllProjectedBy();
    }

    private SolverApi getSolver() {
        return (SolverApi) applicationContext.getBean(solverName);
    }
}
