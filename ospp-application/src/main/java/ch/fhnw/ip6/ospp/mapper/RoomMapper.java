package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.common.dto.RoomDto;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Room;
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import ch.fhnw.ip6.ospp.vo.RoomVO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Mapper
public interface RoomMapper {

    RoomVO fromEntityToVO(Room room);

    RoomDto fromEntityToDto(Room room);

    RoomDto fromVoToDto(RoomVO roomVO);

    Room fromVoToEntity(RoomVO presentationVO);

    default List<Long> mapPresentations(List<Presentation> presentations) {
        if (presentations == null || presentations.isEmpty()) {
            return Collections.emptyList();
        }
        return presentations.stream().map(Presentation::getId).collect(Collectors.toList());
    }


}
