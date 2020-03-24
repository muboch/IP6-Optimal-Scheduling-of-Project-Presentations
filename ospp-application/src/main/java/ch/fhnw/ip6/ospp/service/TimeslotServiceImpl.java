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

            deleteAll();

            // TODO Carlo move delimiter to properties
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().withDelimiter(';').parse(is);

            for (CSVRecord record : records) {

                // TODO Carlo move headers to properties
                Timeslot timeslot = Timeslot.builder()
                        .date(record.get("datum"))
                        .block(Integer.parseInt(record.get("block")))
                        .build();

                timeslotRepository.save(timeslot);
            }

        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        timeslotRepository.deleteAll();
    }
}
