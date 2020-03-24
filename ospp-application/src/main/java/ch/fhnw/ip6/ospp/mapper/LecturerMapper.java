package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.ospp.model.Lecturer;
import org.mapstruct.Mapper;

@Mapper
public interface LecturerMapper {

    ch.fhnw.ip6.common.dto.Lecturer toDto(Lecturer lecturer);

}
