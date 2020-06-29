package ch.fhnw.ip6.ospp.persistence;

import ch.fhnw.ip6.ospp.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface RoomRepository extends JpaRepository<Room, Long> {

}
