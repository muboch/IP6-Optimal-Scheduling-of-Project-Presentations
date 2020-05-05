package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.service.client.TimeslotService;
import ch.fhnw.ip6.ospp.vo.RoomVO;
import ch.fhnw.ip6.ospp.vo.TimeslotVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TimeslotController {

    private final TimeslotService timeslotService;

    @GetMapping("/timeslot")
    public List<TimeslotVO> findAll() {
        return timeslotService.getAll();
    }
    public TimeslotVO findByExternalId(@PathVariable int id) {
        return timeslotService.readByExternalId(id);
    }

}
