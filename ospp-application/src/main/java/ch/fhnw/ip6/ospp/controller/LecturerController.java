package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.mapper.LecturerMapper;
import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.service.LecturerService;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
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

@CrossOrigin
@RestController
@RequestMapping("/lecturer")
@RequiredArgsConstructor
public class LecturerController {

    private final LecturerService lecturerService;
    private final LecturerMapper lecturerMapper;

    @GetMapping
    public ResponseEntity<List<LecturerVO>> all() {
        return ResponseEntity
                .ok()
                .body(lecturerService.getAll().stream().map(lecturerMapper::fromEntityToVo).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LecturerVO> byId(@PathVariable Long id) {
        return lecturerService.findById(id)
                .map(lecturer ->
                        ResponseEntity.ok().body(lecturerMapper.fromEntityToVo(lecturer)))
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    @PostMapping
    public ResponseEntity<LecturerVO> save(@RequestBody Lecturer lecturer) {
        return ResponseEntity
                .ok()
                .body(lecturerMapper.fromEntityToVo(lecturerService.save(lecturer)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        try {
            lecturerService.delete(id);
            return ResponseEntity.ok().body("Lehrperson wurde gelöscht.");
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
