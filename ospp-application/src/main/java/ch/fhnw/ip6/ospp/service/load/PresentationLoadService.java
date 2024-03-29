package ch.fhnw.ip6.ospp.service.load;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Student;
import ch.fhnw.ip6.ospp.service.FachlicheException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

@Log4j2
@Component
@RequiredArgsConstructor
public class PresentationLoadService extends AbstractLoadService {

    private final static String[] headerCols = new String[]{"ID", "Name","Vorname", "Klasse","Vorname2","Name2", "Klasse2", "Titel", "Betreuer", "Experte", "Typ"};

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
                if (cell != null && cell.getCellType() == CellType.BLANK)
                    continue;

                Map<String, Lecturer> lecturersMap = lecturers.stream().collect(Collectors.toMap(Lecturer::getInitials, l -> l));
                String coachInitials = getLecturerInitials(headerMap, row, "Betreuer");
                Lecturer coach = lecturersMap.get(coachInitials);
                if (coach == null) {
                    log.error("no lecturer found for {}", coachInitials);
                    throw new FachlicheException("'" + coachInitials + "' ist nicht in der Liste der Lehrpersonen.");
                }

                String expertInitials = getLecturerInitials(headerMap, row, "Experte");
                Lecturer expert = lecturersMap.get(expertInitials);
                if (expert == null) {
                    log.error("no lecturer found for {}", expertInitials);
                    throw new FachlicheException("'" + expertInitials + "' ist nicht in der Liste der Lehrpersonen.");
                }
                Student studentOne = Student.studentBuilder()
                        .name(row.getCell(headerMap.get("Vorname")).getStringCellValue() + " "+row.getCell(headerMap.get("Name")).getStringCellValue())
                        .schoolclass(row.getCell(headerMap.get("Klasse")).getStringCellValue())
                        .build();

                Presentation presentation = Presentation.builder()
                        .externalId(Integer.parseInt(row.getCell(headerMap.get("ID")).getStringCellValue()))
                        .title(row.getCell(headerMap.get("Titel")).getStringCellValue())
                        .type(row.getCell(headerMap.get("Typ")).getStringCellValue())
                        .studentOne(studentOne)
                        .expert(expert)
                        .coach(coach)
                        .build();

                if (row.getCell(headerMap.get("Name2")) != null && row.getCell(headerMap.get("Vorname2")) != null && StringUtils.isNotBlank(row.getCell(headerMap.get("Name2")).getStringCellValue()) && StringUtils.isNotBlank(row.getCell(headerMap.get("Vorname2")).getStringCellValue())) {
                    Student studentTwo = Student.studentBuilder()
                            .name(row.getCell(headerMap.get("Vorname2")).getStringCellValue() + " " + row.getCell(headerMap.get("Name2")).getStringCellValue())
                            .schoolclass(row.getCell(headerMap.get("Klasse2")).getStringCellValue())
                            .build();
                    presentation.setStudentTwo(studentTwo);
                } else {
                    presentation.setStudentTwo(null);
                }
                presentations.add(presentation);
            }
            return presentations;
        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
        return Collections.emptySet();
    }

    private String getLecturerInitials(HeaderMap headerMap, Row row, String field) {
        return row.getCell(headerMap.get(field)).getStringCellValue().replaceAll("[^\\p{L}\\p{Nd}]+", "").toLowerCase().trim();
    }
}
