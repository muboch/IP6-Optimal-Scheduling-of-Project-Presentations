package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.ospp.model.Room;
import org.mapstruct.Mapper;

@Mapper
public interface RoomMapper {

    ch.fhnw.ip6.common.dto.Room toDto(Room room);

}
