package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Timeslot;
import ch.fhnw.ip6.ospp.persistence.LecturerRepository;
import ch.fhnw.ip6.ospp.persistence.TimeslotRepository;
import ch.fhnw.ip6.ospp.service.client.TimeslotService;
import ch.fhnw.ip6.ospp.vo.TimeslotVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeslotServiceImpl implements TimeslotService {

    private final TimeslotRepository timeslotRepository;
    private final LecturerRepository lecturerRepository;

    @Override
    public Timeslot addTimeslot(Timeslot timeslot) {
        return null;
    }

    @Override
    public Timeslot readById(long id) {
        return null;
    }

    @Override
    public void loadLocktimes(MultipartFile input) {

        try (InputStreamReader is = new InputStreamReader(input.getInputStream(), StandardCharsets.UTF_8)) {
            CSVFormat csvFormat = CSVFormat.EXCEL
                    .withHeader()
                    .withDelimiter(';');

            CSVParser records = csvFormat.parse(is);
            List<String> headers = records.getHeaderNames();
            int timeslots = headers.size();

            for (CSVRecord r : records) {
                Lecturer lecturer = lecturerRepository.readByInitials(r.get(0));
                List<Timeslot> locktimes = new ArrayList<>();
                for (int i = 1; i < timeslots; i++) {
                    String val = r.get(i);
                    if (val.toLowerCase().equals("x")) {
                        Timeslot timeslot = timeslotRepository.findByExternalId(Integer.parseInt(headers.get(i)));
                        locktimes.add(timeslot);
                    }
                }
                lecturer.setLocktimes(locktimes);
                log.warn(lecturer.toString());
                lecturerRepository.save(lecturer);
            }
        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
    }

    @Override
    public void loadTimeslots(MultipartFile input) {

        try (InputStreamReader is = new InputStreamReader(input.getInputStream())) {

            deleteAll();

            // TODO Carlo move delimiter to properties
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader("id", "date", "block", "priority")
                    .withDelimiter(';')
                    .withSkipHeaderRecord().parse(is);

            for (CSVRecord record : records) {

                // TODO Carlo move headers to properties
                Timeslot timeslot = Timeslot.builder()
                        .date(record.get("date"))
                        .externalId(Integer.parseInt(record.get("id")))
                        .block(Integer.parseInt(record.get("block")))
                        .priority(Integer.parseInt(record.get("priority")))
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

    @Override
    public List<TimeslotVO> getAll() {
        return timeslotRepository.findAllProjectedBy();
    }
}
