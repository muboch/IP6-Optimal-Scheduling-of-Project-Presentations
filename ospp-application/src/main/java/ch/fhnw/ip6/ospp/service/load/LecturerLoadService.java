package ch.fhnw.ip6.ospp.service.load;

import ch.fhnw.ip6.ospp.model.Lecturer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
import java.util.Set;

@Log4j2
@Component
@RequiredArgsConstructor
public class LecturerLoadService extends AbstractLoadService {

    private final static String[] headerCols = new String[]{"id", "firstname", "lastname", "initials", "email"};

    public Set<Lecturer> loadLecturer(MultipartFile input) {
        try {

            XSSFWorkbook wb = new XSSFWorkbook(input.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);

            final HeaderMap headerMap = new HeaderMap(headerCols);

            Set<Lecturer> lecturers = new HashSet<>();

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    createHeaderIndexMap(row, headerMap);
                    continue;
                }
                Cell cell = row.getCell(row.getFirstCellNum());
                if (cell != null && cell.getCellType() == CellType.BLANK)
                    continue;

                Lecturer lecturer = Lecturer.lecturerBuilder()
                        .initials(row.getCell(headerMap.get("initials")).getStringCellValue())
                        .email(row.getCell(headerMap.get("email")).getStringCellValue())
                        .lastname(row.getCell(headerMap.get("lastname")).getStringCellValue())
                        .firstname(row.getCell(headerMap.get("firstname")).getStringCellValue())
                        .externalId(Integer.parseInt(row.getCell(headerMap.get("id")).getStringCellValue()))
                        .build();
                lecturers.add(lecturer);

            }
            return lecturers;

        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
        return Collections.emptySet();
    }

}
