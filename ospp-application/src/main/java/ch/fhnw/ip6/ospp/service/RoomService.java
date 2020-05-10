package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.mapper.RoomMapper;
import ch.fhnw.ip6.ospp.model.Room;
import ch.fhnw.ip6.ospp.persistence.RoomRepository;
import ch.fhnw.ip6.ospp.vo.RoomVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public RoomVO save(RoomVO roomVO) {
        Room room = roomMapper.fromVoToEntity(roomVO);
        return roomMapper.fromEntityToVO(roomRepository.save(room));
    }

    public void delete(Long id) {
        roomRepository.deleteById(id);
    }

    public Room save(Room room) {
        return roomRepository.save(room);
    }

    public RoomVO findById(Long id) {
        Optional<Room> byId = roomRepository.findById(id);
        return byId.map(roomMapper::fromEntityToVO).orElseThrow(EntityNotFoundException::new);
    }


    public void deleteAll() {
        roomRepository.deleteAll();
    }

    public List<RoomVO> getAll() {
        return roomRepository.findAllProjectedBy();
    }
}
