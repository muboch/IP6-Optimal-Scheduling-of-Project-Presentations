package ch.fhnw.ip6.ospp.service.client;

import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.ospp.model.CSV;
import ch.fhnw.ip6.ospp.vo.PlanningVO;

import java.util.List;


public interface PlanningService {

    Planning plan() throws Exception;

    CSV getFileById(long id);

    List<PlanningVO> getAllPlannings();

    void firePlanning() throws Exception;
}
