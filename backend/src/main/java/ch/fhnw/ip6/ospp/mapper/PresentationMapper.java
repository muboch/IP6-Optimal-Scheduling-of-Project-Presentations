package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.ospp.model.Presentation;
import org.mapstruct.Mapper;

@Mapper
public interface PresentationMapper {

    ch.fhnw.ip6.common.dto.Presentation toDto(Presentation presentation);

}
