package ch.fhnw.ip6.ospp.service.client;

import ch.fhnw.ip6.ospp.vo.PlanningVO;

import java.util.List;

public interface PlanningService {

    void plan();

    PlanningVO getPlanById(long id);

    List<PlanningVO> getAllPlannings();
}
