package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Timeslot;
import ch.fhnw.ip6.ospp.persistence.LecturerRepository;
import ch.fhnw.ip6.ospp.persistence.TimeslotRepository;
import ch.fhnw.ip6.ospp.service.client.TimeslotService;
import ch.fhnw.ip6.ospp.vo.TimeslotVO;
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

import static org.apache.poi.ss.util.CellReference.convertColStringToIndex;

@Slf4j
@Component
@RequiredArgsConstructor
public class TimeslotServiceImpl extends AbstractService implements TimeslotService {

    private final TimeslotRepository timeslotRepository;
    private final LecturerRepository lecturerRepository;

    @Override
    public Timeslot addTimeslot(Timeslot timeslot) {
        return null;
    }

    @Override
    public Timeslot readById(long id) {
        return null;
    }

    @Override
    public TimeslotVO readByExternalId(long id) {
        return timeslotRepository.findByExternalId(id);
    }

    @Override
    public void loadLocktimes(MultipartFile input) {

        try {

            XSSFWorkbook wb = new XSSFWorkbook(input.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);

            int timeslots = sheet.getRow(0).getLastCellNum();

            final Map<String, Integer> headerMap = new HashMap<>();

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    continue;
                }

                Lecturer lecturer = lecturerRepository.readByInitials(row.getCell(0).getStringCellValue());
                if (lecturer != null) {
                    List<Timeslot> locktimes = new ArrayList<>();
                    for (int i = 1; i < timeslots; i++) {
                        if (row.getCell(i) == null) {
                            continue;
                        }
                        String val = row.getCell(i).getStringCellValue();
                        if (val.toLowerCase().equals("x")) {
                            Timeslot timeslot = timeslotRepository.findByDate(sheet.getRow(0).getCell(i).getStringCellValue());
                            locktimes.add(timeslot);
                        }
                    }
                    lecturer.setLocktimes(locktimes);
                    log.warn(lecturer.toString());
                    lecturerRepository.save(lecturer);
                }
            }
        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
    }

    @Override
    public void loadTimeslots(MultipartFile input) {

        try {

            deleteAll();

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

    @Override
    public void deleteAll() {
        timeslotRepository.deleteAll();
    }

    @Override
    public List<TimeslotVO> getAll() {
        return timeslotRepository.findAllProjectedBy();
    }
}
