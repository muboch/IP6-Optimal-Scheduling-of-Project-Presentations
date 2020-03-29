package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.persistence.LecturerRepository;
import ch.fhnw.ip6.ospp.service.client.LecturerService;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LecturerServiceImpl implements LecturerService {

    private final LecturerRepository lecturerRepository;

    @Override
    public Lecturer addLecturer(Lecturer lecturer) {
        return null;
    }

    @Override
    public Lecturer readById(long id) {
        return null;
    }

    @Override
    public Lecturer readByInitials(String initials) {
        return lecturerRepository.readByInitials(initials);
    }

    @Override
    public void loadLecturer(MultipartFile input) {
        try (InputStreamReader is = new InputStreamReader(input.getInputStream())) {

            deleteAll();

            Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().withDelimiter(';').parse(is);

            for (CSVRecord record : records) {

                // TODO Carlo move headers to properties
                Lecturer expert = Lecturer.lecturerBuilder()
                        .initials(record.get("initials"))
                        .email(record.get("email"))
                        .lastname(record.get("lastname"))
                        .firstname(record.get(0))
                        .build();
                lecturerRepository.save(expert);

            }
        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        lecturerRepository.deleteAll();
    }

    @Override
    public List<LecturerVO> getAll() {
        return lecturerRepository.findAllProjectedBy();
    }
}
