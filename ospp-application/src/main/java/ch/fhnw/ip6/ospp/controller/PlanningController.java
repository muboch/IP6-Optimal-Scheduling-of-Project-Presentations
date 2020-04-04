package ch.fhnw.ip6.ospp.controller;

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
import org.springframework.core.io.FileSystemResource;
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

    @PostMapping(value = "/plannings", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> importFiles(@RequestParam("presentations") MultipartFile presentations,
                                              @RequestParam("teachers") MultipartFile teachers,
                                              @RequestParam("rooms") MultipartFile rooms,
                                              @RequestParam("timeslots") MultipartFile timeslots) {

        deleteTables();
        log.info("previous data truncated");
        loadFiles(presentations, teachers, rooms, timeslots);
        log.info("data upload completed");

        log.info("fire planning event");
        planningService.firePlanning();

        return ResponseEntity.ok().build();

    }

    @GetMapping("/solve")
    public ResponseEntity<Object> solve() {
        planningService.firePlanning();
        return ResponseEntity.ok().build();
    }


    private void loadFiles(@RequestParam("presentations") MultipartFile presentations, @RequestParam("teachers") MultipartFile teachers, @RequestParam("rooms") MultipartFile rooms, @RequestParam("timeslots") MultipartFile timeslots) {
        lecturerService.loadLecturer(teachers);
        presentationService.loadPresentation(presentations);
        roomService.loadRooms(rooms);
        timeslotService.loadTimeslots(timeslots);
    }

    private void deleteTables() {
        presentationService.deleteAll();
        roomService.deleteAll();
        timeslotService.deleteAll();
        lecturerService.deleteAll();
    }

    @GetMapping(value = "/plannings")
    public List<PlanningVO> getPlannings() {
        return planningService.getAllPlannings();
    }

    @GetMapping(value = "/plannings/{id}", produces = "text/csv")
    public ResponseEntity getPlanningById(@PathVariable long id) throws IOException {

        CSV csv = planningService.getFileById(id);

        File file = new File(csv.getName());
        Files.write(file.toPath(), csv.getContent());

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; planning=" + csv.getName() + ".csv")
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new FileSystemResource(file));

    }
}