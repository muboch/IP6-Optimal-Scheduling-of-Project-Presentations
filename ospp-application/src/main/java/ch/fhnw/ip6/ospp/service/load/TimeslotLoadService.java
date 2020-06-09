package ch.fhnw.ip6.ospp.service.load;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Timeslot;
import ch.fhnw.ip6.ospp.persistence.TimeslotRepository;
import ch.fhnw.ip6.ospp.service.LecturerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeslotLoadService extends AbstractLoadService {

    private final static String[] headerCols = new String[]{"id", "date", "block", "priority"};

    public Set<Lecturer> loadOfftimes(MultipartFile input, Set<Lecturer> lecturers, Set<Timeslot> timeslots) {

        try {

            XSSFWorkbook wb = new XSSFWorkbook(input.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);

            int numOfTimeslots = sheet.getRow(0).getLastCellNum();

            Set<Lecturer> offtimesLecturers = new HashSet<>();

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    continue;
                }

                Map<String, Timeslot> timeslotsMap = timeslots.stream().collect(Collectors.toMap(Timeslot::getDate, t -> t));
                Map<String, Lecturer> lecturersMap = lecturers.stream().collect(Collectors.toMap(Lecturer::getInitials, l -> l));
                Lecturer lecturer = lecturersMap.get(row.getCell(0).getStringCellValue());

                if (lecturer != null) {
                    List<Timeslot> offtimes = new ArrayList<>();
                    for (int i = 1; i < numOfTimeslots; i++) {
                        if (row.getCell(i) == null) {
                            continue;
                        }
                        String val = row.getCell(i).getStringCellValue();
                        if (val.toLowerCase().equals("x")) {
                            String cellValue = sheet.getRow(0).getCell(i).getStringCellValue();
                            Timeslot timeslot = timeslotsMap.get(cellValue);
                            offtimes.add(timeslot != null ? timeslot : Timeslot.builder().date(cellValue).build());
                        }
                    }
                    lecturer.setOfftimes(offtimes);
                    offtimesLecturers.add(lecturer);
                }

            }
            return offtimesLecturers;
        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
        return Collections.emptySet();
    }

    public Set<Timeslot> loadTimeslots(MultipartFile input) {

        try {

            XSSFWorkbook wb = new XSSFWorkbook(input.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);

            final HeaderMap headerMap = new HeaderMap(headerCols);

            Set<Timeslot> timeslots = new HashSet<>();
            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    createHeaderIndexMap(row, headerMap);
                    continue;
                }

                Cell cell = row.getCell(row.getFirstCellNum());
                if (cell != null && cell.getCellType() == CellType.BLANK)
                    continue;

                Timeslot timeslot = Timeslot.builder()
                        .date(row.getCell(headerMap.get("date")).getStringCellValue())
                        .externalId(Integer.parseInt(row.getCell(headerMap.get("id")).getStringCellValue()))
                        .block(Integer.parseInt(row.getCell(headerMap.get("block")).getStringCellValue()))
                        .priority((int) row.getCell(headerMap.get("priority")).getNumericCellValue())
                        .build();

                timeslots.add(timeslot);
            }
            return timeslots;
        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
        return Collections.emptySet();
    }
}
