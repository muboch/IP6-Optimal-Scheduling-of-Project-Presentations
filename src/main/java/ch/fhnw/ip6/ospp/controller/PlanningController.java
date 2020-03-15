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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class PlanningController {

    private final PresentationService presentationService;
    private final TeacherService teacherService;
    private final PlanningService planningService;

    enum File {PRESENTATIONS, TEACHERS, TIMESLOTS, ROOMS}

    @PostMapping(value = "/plannings", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> importFiles(@RequestParam MultipartFile[] requestFiles) {

        if (requestFiles.length != 4) {
            log.error("There are 4 files required, but got {}.", requestFiles.length);
            return ResponseEntity.badRequest().build();
        }

        Map<File, MultipartFile> files = mapFiles(requestFiles);

        teacherService.loadTeachers(files.get(File.TEACHERS));
        presentationService.loadPresentation(files.get(File.PRESENTATIONS));

        return ResponseEntity.ok().build();

    }

    @GetMapping(value = "/plannings")
    public List<PlanningVO> getPlannings() {
        List<PlanningVO> plannings = planningService.getAllPlannings();
        return plannings;
    }

    @GetMapping(value = "/plannings/{id}")
    public PlanningVO getPlanningById(@RequestParam long id){
        return planningService.getPlanById(id);
    }

    private Map<File, MultipartFile> mapFiles(MultipartFile[] requestFiles) {
        Map<File, MultipartFile> files = new HashMap<>();
        for (MultipartFile f : requestFiles) {
            if (Objects.requireNonNull(f.getOriginalFilename()).toLowerCase().startsWith("presentation")) {
                files.put(File.PRESENTATIONS, f);
            } else if (f.getOriginalFilename().toLowerCase().startsWith("teacher")) {
                files.put(File.TEACHERS, f);
            } else if (f.getOriginalFilename().toLowerCase().startsWith("time")) {
                files.put(File.TIMESLOTS, f);
            } else if (f.getOriginalFilename().toLowerCase().startsWith("room")) {
                files.put(File.ROOMS, f);
            } else {
                log.error("Cannot match this file: {}", f.getOriginalFilename());
            }
        }
        return files;

    }


}
