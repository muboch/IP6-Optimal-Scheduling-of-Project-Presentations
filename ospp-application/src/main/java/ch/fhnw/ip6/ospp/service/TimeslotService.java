package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.model.Timeslot;
import ch.fhnw.ip6.ospp.persistence.TimeslotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class TimeslotService {

    private final TimeslotRepository timeslotRepository;

    public Timeslot save(Timeslot timeslot) {
        return timeslotRepository.save(timeslot);
    }

    public void delete(Long id) {
        timeslotRepository.deleteById(id);
    }

    public Optional<Timeslot> findById(Long id) {
        return timeslotRepository.findById(id);
    }

    public void deleteAll() {
        timeslotRepository.deleteAllInBatch();
    }

    public List<Timeslot> getAll() {
        return timeslotRepository.findAll();
    }


}
