package ch.fhnw.ip6.ospp.service.load;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Student;
import ch.fhnw.ip6.ospp.persistence.PresentationRepository;
import ch.fhnw.ip6.ospp.service.LecturerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresentationLoadService extends AbstractLoadService {

    private final PresentationRepository presentationRepository;
    private final LecturerService lecturerService;

    public void loadPresentation(MultipartFile input) {

        try {
            XSSFWorkbook wb = new XSSFWorkbook(input.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);

            final Map<String, Integer> headerMap = new HashMap<>();

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    createHeaderIndexMap(row, headerMap);
                    continue;
                }


                Lecturer coach = lecturerService.readByInitials(row.getCell(headerMap.get("coachInitials")).getStringCellValue()).orElse(null);
                Lecturer expert = lecturerService.readByInitials(row.getCell(headerMap.get("expertInitials")).getStringCellValue()).orElse(null);

                Student studentOne = Student.studentBuilder()
                        .name(row.getCell(headerMap.get("name")).getStringCellValue())
                        .schoolclass(row.getCell(headerMap.get("schoolclass")).getStringCellValue())
                        .build();

                Presentation presentation = Presentation.builder()
                        .nr(row.getCell(headerMap.get("nr")).getStringCellValue())
                        .externalId(Integer.parseInt(row.getCell(headerMap.get("id")).getStringCellValue()))
                        .title(row.getCell(headerMap.get("title")).getStringCellValue())
                        .type(row.getCell(headerMap.get("type")).getStringCellValue())
                        .studentOne(studentOne)
                        .expert(expert)
                        .coach(coach)
                        .build();

                if (StringUtils.isNotEmpty(row.getCell(headerMap.get("name2")).getStringCellValue())) {
                    Student studentTwo = Student.studentBuilder()
                            .name(row.getCell(headerMap.get("name2")).getStringCellValue())
                            .schoolclass(row.getCell(headerMap.get("schoolclass")).getStringCellValue())
                            .build();
                    presentation.setStudentTwo(studentTwo);
                }

                presentationRepository.save(presentation);
            }
        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
    }
}
