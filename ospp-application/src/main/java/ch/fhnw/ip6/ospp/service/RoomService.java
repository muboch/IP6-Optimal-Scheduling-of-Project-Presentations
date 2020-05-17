package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.model.Room;
import ch.fhnw.ip6.ospp.persistence.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public Room save(Room room) {
        return roomRepository.save(room);
    }

    public void delete(Long id) {
        roomRepository.deleteById(id);
    }

    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    public void deleteAll() {
        roomRepository.deleteAll();
    }

    public List<Room> getAll() {
        return roomRepository.findAll();
    }
}
