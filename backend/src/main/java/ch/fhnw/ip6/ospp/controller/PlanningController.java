package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.service.client.PlanningService;
import ch.fhnw.ip6.ospp.service.client.PresentationService;
import ch.fhnw.ip6.ospp.service.client.TeacherService;
import ch.fhnw.ip6.ospp.vo.PlanningVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PlanningController {

    private final PresentationService presentationService;
    private final TeacherService teacherService;
    private final PlanningService planningService;

    enum File {PRESENTATIONS, TEACHERS, TIMESLOTS, ROOMS}

    @PostMapping(value = "/plannings", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> importFiles(@RequestParam("presentations") MultipartFile presentations,
                                              @RequestParam("teachers") MultipartFile teachers,
                                              @RequestParam("rooms") MultipartFile rooms,
                                              @RequestParam("timeslots") MultipartFile timeslots) {

        teacherService.loadTeachers(teachers);
        presentationService.loadPresentation(presentations);

        return ResponseEntity.ok().build();

    }

    @GetMapping(value = "/plannings")
    public List<PlanningVO> getPlannings() {
        return planningService.getAllPlannings();
    }

    @GetMapping(value = "/plannings/{id}")
    public PlanningVO getPlanningById(@RequestParam long id) {
        return planningService.getPlanById(id);
    }

    private Map<File, MultipartFile> mapFiles(MultipartFile[] requestFiles) {
        Map<File, MultipartFile> files = new HashMap<>();
        Arrays.stream(requestFiles).forEach(file -> files.put(File.valueOf(file.getName().toUpperCase()), file));
        return files;

    }


}
