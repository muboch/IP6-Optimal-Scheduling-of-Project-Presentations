package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.service.client.LecturerService;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LecturerController {

    private final LecturerService lecturerService;

    @GetMapping("/lecturer")
    public List<LecturerVO> findAll() {
        return lecturerService.getAll();
    }

    @GetMapping("/lecturer/{id}")
    public LecturerVO findByExternalId(@PathVariable int id) {
        return lecturerService.readByExternalId(id);
    }

}
