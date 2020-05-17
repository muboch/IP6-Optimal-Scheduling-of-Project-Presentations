package ch.fhnw.ip6.ospp.persistence;

import ch.fhnw.ip6.ospp.model.Room;
import ch.fhnw.ip6.ospp.vo.RoomVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface RoomRepository extends JpaRepository<Room, Long> {

}
