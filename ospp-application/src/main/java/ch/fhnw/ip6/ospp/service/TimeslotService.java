package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.mapper.TimeslotMapper;
import ch.fhnw.ip6.ospp.model.Lecturer;
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
    private final LecturerRepository lecturerRepository;
    private final TimeslotMapper timeslotMapper;

    public TimeslotVO findById(Long id) {
        Optional<Timeslot> byId = timeslotRepository.findById(id);
        return byId.map(timeslotMapper::fromEntityToVO).orElseThrow(EntityNotFoundException::new);
    }

    public TimeslotVO save(TimeslotVO timeslotVO) {
        Timeslot timeslot = timeslotMapper.fromVoToEntity(timeslotVO);
        return timeslotMapper.fromEntityToVO(timeslotRepository.save(timeslot));
    }

    public void delete(Long id){
        timeslotRepository.deleteById(id);
    }

    public void deleteAll() {
        timeslotRepository.deleteAll();
    }

    public List<TimeslotVO> getAll() {
        return timeslotRepository.findAllProjectedBy();
    }
}
