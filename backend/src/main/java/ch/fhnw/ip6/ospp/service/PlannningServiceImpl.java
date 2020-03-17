package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.service.client.PlanningService;
import ch.fhnw.ip6.ospp.vo.PlanningVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
@Slf4j
@Service
@RequestScope
@RequiredArgsConstructor
public class PlannningServiceImpl implements PlanningService {
    @Override
    public void plan() {

    }

    @Override
    public PlanningVO getPlanById(long id) {
        return null;
    }

    @Override
    public List<PlanningVO> getAllPlannings() {
        return null;
    }
}
