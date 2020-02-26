package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.persistence.PresentationRepository;
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("presentation")
@RequiredArgsConstructor
public class PresentationController {

    private final PresentationRepository presentationRepository;

    @GetMapping
    public List<PresentationVO> findAll() {
        return presentationRepository.findAllProjectedBy();
    }

}
