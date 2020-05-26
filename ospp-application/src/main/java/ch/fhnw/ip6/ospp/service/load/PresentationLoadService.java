package ch.fhnw.ip6.ospp.service.load;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Student;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PresentationLoadService extends AbstractLoadService {

    private final static String[] headerCols = new String[]{"id", "nr", "name", "schoolclass", "name2", "schoolclass2", "title", "coachInitials", "expertInitials", "type"};

    public Set<Presentation> loadPresentation(MultipartFile input, Set<Lecturer> lecturers) {

        try {
            XSSFWorkbook wb = new XSSFWorkbook(input.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);

            final HeaderMap headerMap = new HeaderMap(headerCols);

            Set<Presentation> presentations = new HashSet<>();

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    createHeaderIndexMap(row, headerMap);
                    continue;
                }

                Cell cell = row.getCell(row.getFirstCellNum());
                if (cell != null && cell.getCellType() != CellType.BLANK)
                    continue;

                Map<String, Lecturer> lecturersMap = lecturers.stream().collect(Collectors.toMap(Lecturer::getInitials, l -> l));
                Lecturer coach = lecturersMap.get(row.getCell(headerMap.get("coachInitials")).getStringCellValue().toLowerCase());
                Lecturer expert = lecturersMap.get(row.getCell(headerMap.get("expertInitials")).getStringCellValue().toLowerCase());

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

                if (row.getCell(headerMap.get("name2")) != null) {
                    Student studentTwo = Student.studentBuilder()
                            .name(row.getCell(headerMap.get("name2")).getStringCellValue())
                            .schoolclass(row.getCell(headerMap.get("schoolclass2")).getStringCellValue())
                            .build();
                    presentation.setStudentTwo(studentTwo);
                }
                presentations.add(presentation);
            }
            return presentations;
        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
        return Collections.emptySet();
    }
}
