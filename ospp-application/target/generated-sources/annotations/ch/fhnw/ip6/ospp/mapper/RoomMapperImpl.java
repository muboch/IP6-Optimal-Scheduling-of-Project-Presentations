package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.ospp.vo.RoomVO;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-04-04T17:13:18+0200",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 13.0.2 (N/A)"
)
@Component
public class RoomMapperImpl implements RoomMapper {

    @Override
    public Room toDto(ch.fhnw.ip6.ospp.model.Room room) {
        if ( room == null ) {
            return null;
        }

        Room room1 = new Room();

        room1.setId( room.getExternalId() );
        room1.setName( room.getName() );
        room1.setPlace( room.getPlace() );
        room1.setType( room.getType() );
        room1.setReserve( room.isReserve() );

        return room1;
    }

    @Override
    public Room toDto(RoomVO room) {
        if ( room == null ) {
            return null;
        }

        Room room1 = new Room();

        room1.setId( room.getExternalId() );
        room1.setName( room.getName() );
        room1.setPlace( room.getPlace() );
        room1.setType( room.getType() );
        room1.setReserve( room.isReserve() );

        return room1;
    }
}
