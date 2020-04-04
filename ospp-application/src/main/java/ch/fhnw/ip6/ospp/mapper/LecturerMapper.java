package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface LecturerMapper {

    @Mapping(target = "id", source = "lecturer.externalId")
    ch.fhnw.ip6.common.dto.Lecturer toDto(Lecturer lecturer);

    @Mapping(target = "id", source = "lecturer.externalId")
    ch.fhnw.ip6.common.dto.Lecturer toDto(LecturerVO lecturer);

}
