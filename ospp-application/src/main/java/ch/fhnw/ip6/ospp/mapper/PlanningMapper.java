package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Planning;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
import ch.fhnw.ip6.ospp.vo.PlanningVO;
import org.mapstruct.Mapper;

@Mapper
public interface PlanningMapper {

    ch.fhnw.ip6.common.dto.Planning toDto(Planning planning);

    ch.fhnw.ip6.common.dto.Planning toDto(PlanningVO planning);

    Planning toEntity(ch.fhnw.ip6.common.dto.Planning planning);

}
