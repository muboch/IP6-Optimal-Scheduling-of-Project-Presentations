package ch.fhnw.ip6.ospp.service.client;

import ch.fhnw.ip6.ospp.model.Timeslot;
import org.springframework.web.multipart.MultipartFile;

public interface TimeslotService {


    Timeslot addTimeslot(Timeslot timeslot);

    Timeslot readById(long id);


    void loadTimeslots(MultipartFile file);
}
