package ch.fhnw.ip6.optasolver.mapper;

import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.optasolver.model.Lecturer;
import ch.fhnw.ip6.optasolver.model.Presentation;
import ch.fhnw.ip6.optasolver.model.Room;
import ch.fhnw.ip6.optasolver.model.Timeslot;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface OptaMapper {

    Presentation map(P dto, @MappingTarget Presentation p);

    Lecturer map(L dto, @MappingTarget Lecturer l);

    Timeslot map(T dto, @MappingTarget Timeslot t);

    Room map(R dto, @MappingTarget Room r);

}
