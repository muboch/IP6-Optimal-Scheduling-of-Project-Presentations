package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.service.PresentationService;
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
public class PresentationController {

    private final PresentationService presentationService;

    @GetMapping("/presentation")
    public List<PresentationVO> findAll() {
        return presentationService.getAll();
    }

    @GetMapping("/presentation/{id}")
    public PresentationVO findByExternalId(@PathVariable Long id) {
        return presentationService.findById(id);
    }

}
