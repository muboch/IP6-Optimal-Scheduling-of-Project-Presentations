package ch.fhnw.ip6.ospp.event.listener;

import ch.fhnw.ip6.ospp.event.SolveEvent;
import ch.fhnw.ip6.ospp.service.PlanningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class SolveEventListener implements ApplicationListener<SolveEvent> {

    private final PlanningService planningService;

    @Override
    public void onApplicationEvent(SolveEvent event) {
        log.info("received event for solving");
        try {
            planningService.setSolverName(event.getSolverName());
            planningService.setTestMode(event.getTestMode());
            planningService.setTimeLimit(event.getTimeLimit());
            planningService.plan();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}