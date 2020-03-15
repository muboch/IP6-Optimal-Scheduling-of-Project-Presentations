package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.service.client.PresentationService;
import ch.fhnw.ip6.ospp.service.client.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class ImportController {

    private final PresentationService presentationService;
    private final TeacherService teacherService;

    enum File {PRESENTATIONS, TEACHERS, TIMESLOTS, ROOMS}

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
