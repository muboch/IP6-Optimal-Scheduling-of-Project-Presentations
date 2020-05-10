package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.ospp.mapper.PlanningMapper;
import ch.fhnw.ip6.ospp.model.ExcelFile;
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
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/planning")
@RequiredArgsConstructor
@Slf4j
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

    private final PlanningMapper planningMapper;

    private final SolverContext solverContext;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> importFiles(@RequestParam("presentations") MultipartFile presentations,
                                              @RequestParam("teachers") MultipartFile teachers,
                                              @RequestParam("rooms") MultipartFile rooms,
                                              @RequestParam("timeslots") MultipartFile timeslots,
                                              @RequestParam("locktimes") MultipartFile locktimes) {


        if (solverContext.isSolving()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Solver is already running.");
        }

        deleteTables();
        log.info("previous data truncated");
        loadFiles(presentations, teachers, rooms, timeslots, locktimes);
        log.info("data upload completed");

        log.info("fire planning event");
        try {
            planningService.firePlanning();
            log.info("event fired, solving in process");
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(e.getMessage());
        }

        return ResponseEntity.ok().build();

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


    private void loadFiles(MultipartFile presentations, MultipartFile teachers, MultipartFile rooms, MultipartFile timeslots, MultipartFile locktimes) {
        lecturerLoadService.loadLecturer(teachers);
        presentationLoadService.loadPresentation(presentations);
        roomLoadService.loadRooms(rooms);
        timeslotLoadService.loadTimeslots(timeslots);
        timeslotLoadService.loadOfftimes(locktimes);
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
        ClassPathResource classPathResource = new ClassPathResource("beispieldaten.zip");

        return ResponseEntity.ok()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=beispieldaten.zip")
                .body(classPathResource);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        planningService.delete(id);
    }

}