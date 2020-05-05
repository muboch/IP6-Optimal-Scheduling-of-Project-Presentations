package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.service.LecturerService;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
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
public class LecturerController {

    private final LecturerService lecturerService;

    @GetMapping("/lecturer")
    public List<LecturerVO> all() {
        return lecturerService.getAll();
    }

    @GetMapping("/lecturer/{id}")
    public LecturerVO byId(@PathVariable Long id) {
        return lecturerService.findById(id);
    }

    @PostMapping("/lecturer")
    public LecturerVO save(@RequestParam LecturerVO lecturer) {
        return lecturerService.save(lecturer);
    }

    @DeleteMapping("/lecturer/{id}")
    public void delete(@PathVariable Long id) {
        lecturerService.delete(id);
    }

}
