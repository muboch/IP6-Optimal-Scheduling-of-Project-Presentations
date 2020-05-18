package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.mapper.StudentMapper;
import ch.fhnw.ip6.ospp.model.Room;
import ch.fhnw.ip6.ospp.model.Student;
import ch.fhnw.ip6.ospp.service.StudentService;
import ch.fhnw.ip6.ospp.vo.RoomVO;
import ch.fhnw.ip6.ospp.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final StudentMapper studentMapper;

    @GetMapping
    public ResponseEntity<List<StudentVO>> findAll() {
        return ResponseEntity
                .ok()
                .body(
                        studentService.getAll()
                                .stream().map(studentMapper::fromEntityToVo).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentVO> byId(@PathVariable Long id) {
        return studentService.findById(id)
                .map(student ->
                        ResponseEntity.ok().body(studentMapper.fromEntityToVo(student)))
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    @PostMapping
    public ResponseEntity<StudentVO> save(@RequestBody Student student) {
        return ResponseEntity
                .ok()
                .body(studentMapper.fromEntityToVo(studentService.save(student)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }
}
