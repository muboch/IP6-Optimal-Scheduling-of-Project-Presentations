package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.service.StudentService;
import ch.fhnw.ip6.ospp.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/student")
    public List<StudentVO> findAll() {
        return studentService.getAll();
    }

    @PostMapping("/student")
    public StudentVO save(@RequestParam StudentVO studentVO) {
        return studentService.save(studentVO);
    }

    @DeleteMapping("/student/{id}")
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }
}
