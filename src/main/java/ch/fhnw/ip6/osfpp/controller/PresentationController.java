package ch.fhnw.ip6.osfpp.controller;

import ch.fhnw.ip6.osfpp.model.Presentation;
import ch.fhnw.ip6.osfpp.persistence.PresentationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequestMapping("/presentation")
public class PresentationController {

    @Autowired
    private PresentationRepository presentationRepository;

    @GetMapping("/")
    public List<Presentation> all() {
        return presentationRepository.findAll();
    }

}
