package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.persistence.PresentationRepository;
import ch.fhnw.ip6.ospp.persistence.RoomRepository;
import ch.fhnw.ip6.ospp.persistence.StudentRepository;
import ch.fhnw.ip6.ospp.persistence.LecturerRepository;
import ch.fhnw.ip6.ospp.persistence.TimeslotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("solve")
@RequiredArgsConstructor
@Slf4j
public class SolveController {

    private final PresentationRepository presentationRepository;
    private final LecturerRepository lecturerRepository;
    private final StudentRepository studentRepository;
    private final TimeslotRepository timeslotRepository;
    private final RoomRepository roomRepository;

    @GetMapping
    public ResponseEntity<Object> solve() {
        log.warn("not yet implemented");
        return ResponseEntity.noContent().build();

    }

}
