package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.service.client.RoomService;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
import ch.fhnw.ip6.ospp.vo.RoomVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/room")
    public List<RoomVO> findAll() {
        return roomService.getAll();
    }
    public RoomVO findByExternalId(@PathVariable int id) {
        return roomService.readByExternalId(id);
    }

}
