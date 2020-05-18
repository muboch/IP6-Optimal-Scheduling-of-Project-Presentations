package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.mapper.RoomMapper;
import ch.fhnw.ip6.ospp.model.Room;
import ch.fhnw.ip6.ospp.service.RoomService;
import ch.fhnw.ip6.ospp.vo.RoomVO;
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

@RestController
@CrossOrigin
@RequestMapping("/api/room")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;

    @GetMapping
    public ResponseEntity<List<RoomVO>> findAll() {
        return ResponseEntity
                .ok()
                .body(
                        roomService.getAll()
                                .stream().map(roomMapper::fromEntityToVo).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomVO> byId(@PathVariable Long id) {
        return roomService.findById(id)
                .map(presentaion ->
                        ResponseEntity.ok().body(roomMapper.fromEntityToVo(presentaion)))
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    @PostMapping
    public ResponseEntity<RoomVO> save(@RequestBody Room room) {
        return ResponseEntity
                .ok()
                .body(roomMapper.fromEntityToVo(roomService.save(room)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        roomService.delete(id);
    }
}
