package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Student;
import ch.fhnw.ip6.ospp.model.Teacher;
import ch.fhnw.ip6.ospp.persistence.PresentationRepository;
import ch.fhnw.ip6.ospp.service.client.PresentationService;
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
public class PresentationServiceImpl implements PresentationService {

    private final PresentationRepository presentationRepository;
    private TeacherService teacherService;


    @Override
    public void loadPresentation(MultipartFile input) {

        try (InputStreamReader is = new InputStreamReader(input.getInputStream())) {
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(is);
            for (CSVRecord record : records) {

                Teacher expert = teacherService.readByInitials(record.get("Experte"));
                Teacher coach = teacherService.readByInitials(record.get("Betreuung"));

                Student studentOne = Student.studentBuilder().lastname(record.get("Name_1")).firstname(record.get("Vorname_1")).schoolClass(record.get("Klasse_1")).build();
                Student studentTwo = Student.studentBuilder().lastname(record.get("Name_2")).firstname(record.get("Vorname_2")).schoolClass(record.get("Klasse_2")).build();

                Presentation presentation = Presentation.builder().nr(record.get("Nr.")).title(record.get("Titel"))
                        .firstStudent(studentOne)
                        .secondStudent(studentTwo)
                        .expert(expert)
                        .coach(coach)
                        .build();
                presentationRepository.save(presentation);
            }
        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
    }

    @Override
    public Presentation addPresentation(Presentation presentation) {
        return null;
    }

    @Override
    public Presentation readById(long id) {
        return null;
    }

    @Override
    public Presentation readByNr(String nr) {
        return null;
    }
}
