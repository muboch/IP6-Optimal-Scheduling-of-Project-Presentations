package ch.fhnw.ip6.ospp.service.client;

import ch.fhnw.ip6.ospp.model.Timeslot;
import ch.fhnw.ip6.ospp.vo.TimeslotVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TimeslotService {

    Timeslot addTimeslot(Timeslot timeslot);

    Timeslot readById(long id);
    TimeslotVO readByExternalId(long id);

    void loadTimeslots(MultipartFile file);

    void loadLocktimes(MultipartFile file);

    void deleteAll();

    List<TimeslotVO> getAll();
}
