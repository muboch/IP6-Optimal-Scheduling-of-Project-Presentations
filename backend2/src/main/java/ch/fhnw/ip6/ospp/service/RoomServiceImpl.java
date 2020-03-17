package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.model.Room;
import ch.fhnw.ip6.ospp.persistence.RoomRepository;
import ch.fhnw.ip6.ospp.service.client.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Service
@RequestScope
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;


    @Override
    public Room addRoom(Room room) {
        return null;
    }

    @Override
    public Room readById(long id) {
        return null;
    }

    @Override
    public void loadRooms(MultipartFile input) {
        try (InputStreamReader is = new InputStreamReader(input.getInputStream())) {
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(is);
            for (CSVRecord record : records) {
                Room room = Room.builder().roomNumber(record.get("Nr.")).roomType(record.get("Typ")).build();
                roomRepository.save(room);
            }
        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
    }
}