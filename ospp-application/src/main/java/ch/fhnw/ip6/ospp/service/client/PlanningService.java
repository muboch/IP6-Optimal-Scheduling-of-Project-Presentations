package ch.fhnw.ip6.ospp.service.client;

import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.ospp.vo.PlanningVO;

import java.util.List;


public interface PlanningService {

    Planning plan();

    byte[] getFileById(long id);

    List<PlanningVO> getAllPlannings();
}
