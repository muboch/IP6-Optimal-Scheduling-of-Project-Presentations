package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.model.Teacher;
import ch.fhnw.ip6.ospp.persistence.TeacherRepository;
import ch.fhnw.ip6.ospp.service.client.TeacherService;
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
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;

    @Override
    public Teacher addTeacher(Teacher teacher) {
        return null;
    }

    @Override
    public Teacher readById(long id) {
        return null;
    }

    @Override
    public Teacher readByInitials(String initials) {
        return teacherRepository.readByInitials(initials);
    }

    @Override
    public void loadTeachers(MultipartFile input) {
        try (InputStreamReader is = new InputStreamReader(input.getInputStream())) {

            deleteAll();

            Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().withDelimiter(';').parse(is);

            for (CSVRecord record : records) {

                // TODO Carlo move headers to properties
                Teacher expert = Teacher.teacherBuilder()
                        .initials(record.get("initials"))
                        .email(record.get("email"))
                        .lastname(record.get("lastname"))
                        .firstname(record.get(0))
                        .build();
                teacherRepository.save(expert);

            }
        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        teacherRepository.deleteAll();
    }
}
