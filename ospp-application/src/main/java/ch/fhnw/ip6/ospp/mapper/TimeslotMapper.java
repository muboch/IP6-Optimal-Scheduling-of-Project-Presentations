package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.ospp.model.Timeslot;
import ch.fhnw.ip6.ospp.vo.TimeslotVO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface TimeslotMapper {

    ch.fhnw.ip6.common.dto.Timeslot toDto(Timeslot timeslot);

    ch.fhnw.ip6.common.dto.Timeslot toDto(TimeslotVO timeslot);


}
