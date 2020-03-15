package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.model.Timeslot;
import ch.fhnw.ip6.ospp.persistence.TimeslotRepository;
import ch.fhnw.ip6.ospp.service.client.TimeslotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequestScope
@RequiredArgsConstructor
public class TimeslotServiceImpl implements TimeslotService {

    private final TimeslotRepository timeslotRepository;


    @Override
    public Timeslot addTimeslot(Timeslot timeslot) {
        return null;
    }

    @Override
    public Timeslot readById(long id) {
        return null;
    }

    @Override
    public void loadTimeslots(MultipartFile input) {
        try (InputStreamReader is = new InputStreamReader(input.getInputStream())) {
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(is);
            for (CSVRecord record : records) {
                Timeslot timeslot = Timeslot.builder().start(LocalDateTime.parse(record.get("Start"))).build();
                timeslotRepository.save(timeslot);
            }
        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
    }
}
