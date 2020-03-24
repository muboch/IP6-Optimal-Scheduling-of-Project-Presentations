package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.persistence.LecturerRepository;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("lecturer")
@RequiredArgsConstructor
public class LecturerController {

    private final LecturerRepository lecturerRepository;

    @GetMapping
    public List<LecturerVO> findAll() {
        return lecturerRepository.findAllProjectedBy();
    }

}
