package ch.fhnw.ip6.ospp.service.load;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.persistence.LecturerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class LecturerLoadService extends AbstractLoadService {

    private final LecturerRepository lecturerRepository;

    public void loadLecturer(MultipartFile input) {
        try {

            XSSFWorkbook wb = new XSSFWorkbook(input.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);

            final Map<String, Integer> headerMap = new HashMap<>();

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    createHeaderIndexMap(row, headerMap);
                    continue;
                }

                Lecturer expert = Lecturer.lecturerBuilder()
                        .initials(row.getCell(headerMap.get("initials")).getStringCellValue())
                        .email(row.getCell(headerMap.get("email")).getStringCellValue())
                        .lastname(row.getCell(headerMap.get("lastname")).getStringCellValue())
                        .firstname(row.getCell(headerMap.get("firstname")).getStringCellValue())
                        .externalId(Integer.parseInt(row.getCell(headerMap.get("id")).getStringCellValue()))
                        .build();
                lecturerRepository.save(expert);

            }

        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
    }

}