package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.ospp.model.Planning;
import ch.fhnw.ip6.ospp.vo.PlanningVO;
import org.mapstruct.Mapper;

@Mapper
public interface PlanningMapper {

    PlanningVO toVO(Planning planning);

}
