package ch.fhnw.ip6.ospp.service.client;

import ch.fhnw.ip6.ospp.model.Room;
import org.springframework.web.multipart.MultipartFile;

public interface RoomService {


    Room addRoom(Room room);

    Room readById(long id);


    void loadRooms(MultipartFile file);

    void deleteAll();
}
