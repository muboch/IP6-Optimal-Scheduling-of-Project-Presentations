package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.ospp.mapper.PlanningMapper;
import ch.fhnw.ip6.ospp.model.ExcelFile;
import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Room;
import ch.fhnw.ip6.ospp.model.Timeslot;
import ch.fhnw.ip6.ospp.service.ConsistencyError;
import ch.fhnw.ip6.ospp.service.ConsistencyService;
import ch.fhnw.ip6.ospp.service.LecturerService;
import ch.fhnw.ip6.ospp.service.PlanningService;
import ch.fhnw.ip6.ospp.service.PresentationService;
import ch.fhnw.ip6.ospp.service.RoomService;
import ch.fhnw.ip6.ospp.service.TimeslotService;
import ch.fhnw.ip6.ospp.service.load.LecturerLoadService;
import ch.fhnw.ip6.ospp.service.load.PresentationLoadService;
import ch.fhnw.ip6.ospp.service.load.RoomLoadService;
import ch.fhnw.ip6.ospp.service.load.TimeslotLoadService;
import ch.fhnw.ip6.ospp.vo.PlanningVO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.fhnw.ip6.ospp.service.ConsistencyError.Status.ERROR;

@RestController
@CrossOrigin
@RequestMapping("/api/planning")
@RequiredArgsConstructor
@Log4j2
public class PlanningController {

    private final PresentationService presentationService;
    private final LecturerService lecturerService;
    private final PlanningService planningService;
    private final RoomService roomService;
    private final TimeslotService timeslotService;

    private final PresentationLoadService presentationLoadService;
    private final LecturerLoadService lecturerLoadService;
    private final RoomLoadService roomLoadService;
    private final TimeslotLoadService timeslotLoadService;
    private final ConsistencyService consistencyService;

    private final PlanningMapper planningMapper;

    private final SolverContext solverContext;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> importFiles(@RequestParam("presentations") MultipartFile presentations,
                                              @RequestParam("teachers") MultipartFile teachers,
                                              @RequestParam("rooms") MultipartFile rooms,
                                              @RequestParam("timeslots") MultipartFile timeslots,
                                              @RequestParam("locktimes") MultipartFile locktimes) {

        List<ConsistencyError> errors = loadFiles(presentations, teachers, rooms, timeslots, locktimes);

        if (errors.isEmpty() || errors.stream().noneMatch(e -> e.getStatus() == ERROR)) {
            log.info("data upload completed");
            return ResponseEntity.ok().body(errors);
        }

        return ResponseEntity.badRequest().body(errors);

    }

    @GetMapping("/solve")
    public ResponseEntity<Object> solve() {
        log.info("fire planning event");
        try {
            planningService.firePlanning();
            log.info("event fired, solving in process");
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.PROCESSING).body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }


    private List<ConsistencyError> loadFiles(MultipartFile presentationsInput, MultipartFile lecturersInput, MultipartFile roomsInput, MultipartFile timeslotsInput, MultipartFile offtimesInput) {
        Set<Lecturer> lecturers = lecturerLoadService.loadLecturer(lecturersInput);
        Set<Presentation> presentations = presentationLoadService.loadPresentation(presentationsInput, lecturers);
        Set<Room> rooms = roomLoadService.loadRooms(roomsInput);
        Set<Timeslot> timeslots = timeslotLoadService.loadTimeslots(timeslotsInput);
        Set<Lecturer> offtimesLectrures = timeslotLoadService.loadOfftimes(offtimesInput, lecturers, timeslots);
        Set<Timeslot> offtimesTimeslots = offtimesLectrures.stream().map(Lecturer::getOfftimes).flatMap(List::stream).collect(Collectors.toSet());

        log.info("check for consistency");
        List<ConsistencyError> errors = consistencyService.checkConsistencyOfLecturers(presentations, lecturers, offtimesLectrures);
        errors.addAll(consistencyService.checkConsistencyOfTimeslots(timeslots, offtimesTimeslots));

        if (errors.isEmpty() || errors.stream().noneMatch(e -> e.getStatus() == ERROR)) {
            log.info("import data is consistent");

            deleteTables();
            log.info("previous data truncated");

            lecturers.forEach(lecturerService::save);
            presentations.forEach(presentationService::save);
            rooms.forEach(roomService::save);
            timeslots.forEach(timeslotService::save);

        }
        return errors;

    }

    private void deleteTables() {
        presentationService.deleteAll();
        roomService.deleteAll();
        lecturerService.deleteAll();
        timeslotService.deleteAll();
    }

    @GetMapping
    public ResponseEntity<List<PlanningVO>> findAll() {
        return ResponseEntity
                .ok()
                .body(
                        planningService.getAll()
                                .stream().map(planningMapper::toVO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadCsv(@PathVariable long id) throws IOException {

        // Load file as Resource
        ExcelFile excelFile = planningService.getFileById(id);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + excelFile.getName() + "\"")
                .body(new ByteArrayResource(excelFile.getContent()));
    }

    @GetMapping("/example")
    public ResponseEntity<Resource> downloadExample() throws IOException {

        // Load file as Resource
        ClassPathResource classPathResource = new ClassPathResource("data/beispieldaten.zip");

        return ResponseEntity.ok()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=beispieldaten.zip")
                .body(classPathResource);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        planningService.delete(id);
    }

    @GetMapping("/consistency")
    public ResponseEntity<List<ConsistencyError>> consistency() {
        Set<Presentation> presentations = new HashSet<>(presentationService.getAll());
        Set<Lecturer> lecturers = new HashSet<>(lecturerService.getAll());

        List<ConsistencyError> errors = consistencyService.checkConsistencyOfPresentations(presentations);
        errors.addAll(consistencyService.checkConsistencyOfLecturers(presentations, lecturers, Collections.emptySet()));

        return ResponseEntity.ok().body(errors);
    }

    @GetMapping("/isSolving")
    @ResponseBody
    public Solving isSolving() {
        Solving solving = new Solving();
        solving.isSolving = solverContext.isSolving();
        int timeLimit = solverContext.getTimeLimit();
        if (solverContext.isSolving()) {
            long startTime = solverContext.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long endTime = solverContext.getStartTime().plusSeconds(timeLimit).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long currentTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long duration = endTime - startTime; // 100%
            long passed = currentTime - startTime; // progress
            solving.progress = Double.valueOf(passed * 100.0 / duration).intValue();
        }
        return solving;
    }

    @Setter
    @Getter
    public class Solving {
        boolean isSolving;
        int progress;
    }

}