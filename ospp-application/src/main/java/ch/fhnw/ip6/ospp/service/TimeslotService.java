package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.mapper.TimeslotMapper;
import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Room;
import ch.fhnw.ip6.ospp.model.Timeslot;
import ch.fhnw.ip6.ospp.persistence.LecturerRepository;
import ch.fhnw.ip6.ospp.persistence.TimeslotRepository;
import ch.fhnw.ip6.ospp.vo.TimeslotVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeslotService   {

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
        timeslotRepository.deleteAll();
    }

    public List<Timeslot> getAll() {
        return timeslotRepository.findAll();
    }


}
