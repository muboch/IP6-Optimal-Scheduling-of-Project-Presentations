package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.service.RoomService;
import ch.fhnw.ip6.ospp.vo.RoomVO;
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
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/room")
    public List<RoomVO> findAll() {
        return roomService.getAll();
    }

    @GetMapping("/room/{id}")
    public RoomVO findById(@PathVariable Long id) {
        return roomService.findById(id);
    }

    @PostMapping("/room")
    public RoomVO save(@RequestParam RoomVO roomVO) {
        return roomService.save(roomVO);
    }

    @DeleteMapping("/student/{id}")
    public void delete(@PathVariable Long id) {
        roomService.delete(id);
    }
}
