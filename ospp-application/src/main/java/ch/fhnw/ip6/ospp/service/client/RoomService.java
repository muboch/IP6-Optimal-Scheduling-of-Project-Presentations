package ch.fhnw.ip6.ospp.service.client;

import ch.fhnw.ip6.ospp.model.Room;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
import ch.fhnw.ip6.ospp.vo.RoomVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface RoomService {


    Room addRoom(Room room);

    Room readById(long id);

    RoomVO readByExternalId(int id);



    void loadRooms(MultipartFile file);

    void deleteAll();

    List<RoomVO> getAll();
}
