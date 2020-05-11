package ch.fhnw.ip6.ospp.service.load;

import ch.fhnw.ip6.ospp.model.Room;
import ch.fhnw.ip6.ospp.persistence.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoomLoadService extends AbstractLoadService {

    private final RoomRepository roomRepository;

    public Set<Room> loadRooms(MultipartFile input) {
        try {

            XSSFWorkbook wb = new XSSFWorkbook(input.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);

            final Map<String, Integer> headerMap = new HashMap<>();

            Set<Room> rooms = new HashSet<>();

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    createHeaderIndexMap(row, headerMap);
                    continue;
                }


                Room room = Room.builder()
                        .name(row.getCell(headerMap.get("name")).getStringCellValue())
                        .place(row.getCell(headerMap.get("place")).getStringCellValue())
                        .externalId(Integer.parseInt(row.getCell(headerMap.get("id")).getStringCellValue()))
                        .type(row.getCell(headerMap.get("type")).getStringCellValue())
                        .reserve(Boolean.parseBoolean(row.getCell(headerMap.get("reserve")).getStringCellValue()))
                        .build();
                rooms.add(room);
            }

            return rooms;
        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
        return Collections.emptySet();
    }

}
