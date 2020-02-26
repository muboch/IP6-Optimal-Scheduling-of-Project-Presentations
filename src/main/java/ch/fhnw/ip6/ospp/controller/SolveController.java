package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.persistence.PresentationRepository;
import ch.fhnw.ip6.ospp.persistence.RoomRepository;
import ch.fhnw.ip6.ospp.persistence.StudentRepository;
import ch.fhnw.ip6.ospp.persistence.TeacherRepository;
import ch.fhnw.ip6.ospp.persistence.TimeslotRepository;
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("solve")
@RequiredArgsConstructor
public class SolveController {

    private final PresentationRepository presentationRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final TimeslotRepository timeslotRepository;
    private final RoomRepository roomRepository;

    @GetMapping
    public List<PresentationVO> solve() {

        return Collections.emptyList();
    }

}
