package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.ospp.model.Room;
import ch.fhnw.ip6.ospp.vo.RoomVO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface RoomMapper {

    ch.fhnw.ip6.common.dto.Room toDto(Room room);

    ch.fhnw.ip6.common.dto.Room toDto(RoomVO room);

}
