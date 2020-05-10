package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.service.TimeslotService;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
import org.mapstruct.Mapper;

@Mapper(uses = TimeslotService.class)
public interface LecturerMapper {

    LecturerDto fromEntityToDto(Lecturer lecturer);

    LecturerVO fromEntityToVo(Lecturer lecturer);

    Lecturer fromVoToEntity(LecturerVO lecturer);

}
