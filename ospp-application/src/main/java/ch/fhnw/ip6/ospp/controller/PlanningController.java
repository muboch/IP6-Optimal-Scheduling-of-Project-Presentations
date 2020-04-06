package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.ospp.model.CSV;
import ch.fhnw.ip6.ospp.service.PlannningServiceImpl;
import ch.fhnw.ip6.ospp.service.client.LecturerService;
import ch.fhnw.ip6.ospp.service.client.PlanningService;
import ch.fhnw.ip6.ospp.service.client.PresentationService;
import ch.fhnw.ip6.ospp.service.client.RoomService;
import ch.fhnw.ip6.ospp.service.client.TimeslotService;
import ch.fhnw.ip6.ospp.vo.PlanningVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PlanningController {

    private final PresentationService presentationService;
    private final LecturerService lecturerService;
    private final PlanningService planningService;
    private final RoomService roomService;
    private final TimeslotService timeslotService;

    private final SolverContext solverContext;

    @PostMapping(value = "/plannings", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
        lecturerService.loadLecturer(teachers);
        presentationService.loadPresentation(presentations);
        roomService.loadRooms(rooms);
        timeslotService.loadTimeslots(timeslots);
        timeslotService.loadLocktimes(locktimes);
    }

    private void deleteTables() {
        presentationService.deleteAll();
        roomService.deleteAll();
        lecturerService.deleteAll();
        timeslotService.deleteAll();
    }

    @GetMapping(value = "/plannings")
    public List<PlanningVO> getPlannings() {
        return planningService.getAllPlannings();
    }

    @GetMapping("/plannings/{id}")
    public ResponseEntity<Resource> downloadCsv(@PathVariable long id) throws IOException {

        // Load file as Resource
        CSV csv = planningService.getFileById(id);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + csv.getName() + ".csv" + "\"")
                .body(new ByteArrayResource(csv.getContent()));
    }

    @GetMapping("/plannings/example")
    public ResponseEntity<Resource> downloadExample() throws IOException {

        // Load file as Resource
        ClassPathResource classPathResource = new ClassPathResource("beispieldaten.zip");

        return ResponseEntity.ok()
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=beispieldaten.zip")
                .body(classPathResource);
    }

}