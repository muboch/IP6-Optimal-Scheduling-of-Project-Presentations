package ch.fhnw.ip6.ospp.event.listener;

import ch.fhnw.ip6.ospp.event.SolveEvent;
import ch.fhnw.ip6.ospp.service.client.PlanningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SolveEventListener implements ApplicationListener<SolveEvent> {

    @Autowired
    private PlanningService planningService;

    @Override
    public void onApplicationEvent(SolveEvent event) {
        log.info("received event for solving");
        try {
            planningService.plan();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}