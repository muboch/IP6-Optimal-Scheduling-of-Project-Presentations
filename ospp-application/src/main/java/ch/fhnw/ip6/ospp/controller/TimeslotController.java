package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.service.TimeslotService;
import ch.fhnw.ip6.ospp.vo.TimeslotVO;
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
public class TimeslotController {

    private final TimeslotService timeslotService;

    @GetMapping("/timeslot")
    public List<TimeslotVO> findAll() {
        return timeslotService.getAll();
    }

    @GetMapping("/timeslot/{id}")
    public TimeslotVO findByExternalId(@PathVariable Long id) {
        return timeslotService.findById(id);
    }


    @PostMapping("/timeslot")
    public TimeslotVO save(@RequestParam TimeslotVO timeslotVO) {
        return timeslotService.save(timeslotVO);
    }

    @DeleteMapping("/timeslot/{id}")
    public void delete(@PathVariable Long id) {
        timeslotService.delete(id);
    }

}
