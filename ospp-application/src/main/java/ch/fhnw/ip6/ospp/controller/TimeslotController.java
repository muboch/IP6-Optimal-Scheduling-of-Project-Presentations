package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.mapper.TimeslotMapper;
import ch.fhnw.ip6.ospp.model.Timeslot;
import ch.fhnw.ip6.ospp.service.TimeslotService;
import ch.fhnw.ip6.ospp.vo.TimeslotVO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api/timeslot")
@RequiredArgsConstructor
public class TimeslotController {

    private final TimeslotService timeslotService;
    private final TimeslotMapper timeslotMapper;

    @GetMapping
    public ResponseEntity<List<TimeslotVO>> findAll() {
        return ResponseEntity
                .ok()
                .body(
                        timeslotService.getAll()
                                .stream().map(timeslotMapper::fromEntityToVo).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeslotVO> byId(@PathVariable Long id) {
        return timeslotService.findById(id)
                .map(presentaion ->
                        ResponseEntity.ok().body(timeslotMapper.fromEntityToVo(presentaion)))
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    @PostMapping
    public ResponseEntity<TimeslotVO> save(@RequestBody Timeslot timeslot) {
        return ResponseEntity
                .ok()
                .body(timeslotMapper.fromEntityToVo(timeslotService.save(timeslot)));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        timeslotService.delete(id);
    }

}
