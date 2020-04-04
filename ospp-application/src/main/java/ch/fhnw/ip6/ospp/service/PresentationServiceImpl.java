package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Student;
import ch.fhnw.ip6.ospp.model.Type;
import ch.fhnw.ip6.ospp.persistence.PresentationRepository;
import ch.fhnw.ip6.ospp.service.client.LecturerService;
import ch.fhnw.ip6.ospp.service.client.PresentationService;
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresentationServiceImpl implements PresentationService {

    private final PresentationRepository presentationRepository;
    private final LecturerService lecturerService;


    @Override
    public void loadPresentation(MultipartFile input) {

        try (InputStreamReader is = new InputStreamReader(input.getInputStream())) {

            deleteAll();

            // TODO Carlo move delimiter to properties
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().withDelimiter(';').parse(is);

            for (CSVRecord record : records) {

                // TODO Carlo move headers to properties
                Lecturer expert = lecturerService.readByInitials(record.get("coachInitials"));
                Lecturer coach = lecturerService.readByInitials(record.get("expertInitials"));

                Student studentOne = Student.studentBuilder()
                        .name(record.get("name"))
                        .schoolclass(record.get("schoolclass"))
                        .build();

                Presentation presentation = Presentation.builder()
                        .nr(record.get("nr"))
                        .externalId(Integer.parseInt(record.get(0)))
                        .title(record.get("title"))
                        .type(record.get("type"))
                        .studentOne(studentOne)
                        .expert(expert)
                        .coach(coach)
                        .build();

                if (StringUtils.isNotEmpty(record.get("name2"))) {
                    Student studentTwo = Student.studentBuilder()
                            .name(record.get("name2"))
                            .schoolclass(record.get("schoolclass2"))
                            .build();
                    presentation.setStudentTwo(studentTwo);
                }

                presentationRepository.save(presentation);
            }
        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
    }

    @Override
    public PresentationVO addPresentation(Presentation presentation) {
        return null;
    }

    @Override
    public PresentationVO readById(long id) {
        return null;
    }

    @Override
    public PresentationVO readByNr(String nr) {
        return null;
    }

    @Override
    public void deleteAll() {
        presentationRepository.deleteAll();
    }

    @Override
    public List<PresentationVO> getAll() {
        return presentationRepository.findAllProjectedBy();
    }
}
