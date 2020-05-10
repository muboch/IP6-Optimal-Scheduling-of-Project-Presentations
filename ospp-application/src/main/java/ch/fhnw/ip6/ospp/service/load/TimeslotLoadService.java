package ch.fhnw.ip6.ospp.service.load;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Timeslot;
import ch.fhnw.ip6.ospp.persistence.TimeslotRepository;
import ch.fhnw.ip6.ospp.service.LecturerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeslotLoadService extends AbstractLoadService {

    private final LecturerService lecturerService;
    private final TimeslotRepository timeslotRepository;

    public void loadOfftimes(MultipartFile input) {

        try {

            XSSFWorkbook wb = new XSSFWorkbook(input.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);

            int timeslots = sheet.getRow(0).getLastCellNum();

            final Map<String, Integer> headerMap = new HashMap<>();

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    continue;
                }

                Lecturer lecturer = lecturerService.readByInitials(row.getCell(0).getStringCellValue());
                if (lecturer != null) {
                    List<Timeslot> offtimes = new ArrayList<>();
                    for (int i = 1; i < timeslots; i++) {
                        if (row.getCell(i) == null) {
                            continue;
                        }
                        String val = row.getCell(i).getStringCellValue();
                        if (val.toLowerCase().equals("x")) {
                            Timeslot timeslot = timeslotRepository.findByDate(sheet.getRow(0).getCell(i).getStringCellValue());
                            offtimes.add(timeslot);
                        }
                    }
                    lecturer.setOfftimes(offtimes);
                    log.warn(lecturer.toString());
                    lecturerService.save(lecturer);
                }
            }
        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
    }

    public void loadTimeslots(MultipartFile input) {

        try {

            XSSFWorkbook wb = new XSSFWorkbook(input.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);

            final Map<String, Integer> headerMap = new HashMap<>();

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    createHeaderIndexMap(row, headerMap);
                    continue;
                }

                Timeslot timeslot = Timeslot.builder()
                        .date(row.getCell(headerMap.get("date")).getStringCellValue())
                        .externalId(Integer.parseInt(row.getCell(headerMap.get("id")).getStringCellValue()))
                        .block(Integer.parseInt(row.getCell(headerMap.get("block")).getStringCellValue()))
                        .priority((int) row.getCell(headerMap.get("priority")).getNumericCellValue())
                        .build();

                timeslotRepository.save(timeslot);
            }

        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
    }
}
