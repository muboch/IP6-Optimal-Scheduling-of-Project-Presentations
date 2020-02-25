package ch.fhnw.ip6.osfpp.controller;

import ch.fhnw.ip6.osfpp.model.Room;
import ch.fhnw.ip6.osfpp.persistence.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequestMapping("room")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping
    public String findAll(Model model) {
        model.addAttribute("rooms", roomRepository.findAll());
        return "room";
    }
}
