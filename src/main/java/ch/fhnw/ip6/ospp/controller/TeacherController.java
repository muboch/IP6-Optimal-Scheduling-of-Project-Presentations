package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.persistence.TeacherRepository;
import ch.fhnw.ip6.ospp.vo.TeacherVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("teacher")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherRepository teacherRepository;

    @GetMapping
    public List<TeacherVO> findAll() {
        return teacherRepository.findAllProjectedBy();
    }

}
