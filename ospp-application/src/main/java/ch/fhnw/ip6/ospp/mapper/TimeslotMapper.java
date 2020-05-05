package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.common.dto.TimeslotDto;
import ch.fhnw.ip6.ospp.model.Timeslot;
import ch.fhnw.ip6.ospp.vo.TimeslotVO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface TimeslotMapper {

    TimeslotVO fromEntityToVO(Timeslot byId);

    TimeslotDto fromEntityToDto(Timeslot timeslot);
}
