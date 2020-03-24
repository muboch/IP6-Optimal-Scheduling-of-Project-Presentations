package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.ospp.model.Timeslot;
import org.mapstruct.Mapper;

@Mapper
public interface TimeslotMapper {

    ch.fhnw.ip6.common.dto.Timeslot toDto(Timeslot timeslot);

}
