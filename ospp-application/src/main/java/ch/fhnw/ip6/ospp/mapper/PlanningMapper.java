package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.ospp.model.Planning;
import ch.fhnw.ip6.ospp.vo.PlanningVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.format.DateTimeFormatter;

@Mapper(imports = DateTimeFormatter.class)
public interface PlanningMapper {

    @Mapping(target = "created", expression = "java(planning.getCreated().format(DateTimeFormatter.ofPattern(\"HH:mm:ss dd.MM.YYYY\")))")
    PlanningVO toVO(Planning planning);

}
