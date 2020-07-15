package ch.fhnw.ip6.ospp.controller;


import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.ospp.event.SolveEvent;
import ch.fhnw.ip6.ospp.event.SolveEvent.TestMode;
import ch.fhnw.ip6.ospp.service.FachlicheException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.fhnw.ip6.ospp.event.SolveEvent.TestMode.LARGE;
import static ch.fhnw.ip6.ospp.event.SolveEvent.TestMode.NORMAL;

@CrossOrigin
@RestController
@RequestMapping("/api/measuring")
@RequiredArgsConstructor
@Log4j2
public class MeasuringController {

    private final SolverContext solverContext;
    private final ApplicationEventPublisher applicationEventPublisher;

    @GetMapping
    public ResponseEntity<String[]> measuring() {
        String[] endpoints = {"ortools", "ilp", "opta"};
        return ResponseEntity.ok(Arrays.stream(endpoints).map(e -> "/api/measuring/" + e).toArray(String[]::new));
    }

    @GetMapping("ortools300")
    public void solveOrToolsLarge() throws Exception {
        solve("ch.fhnw.ip6.ortoolssolver.Solver", LARGE);
    }

    @GetMapping("ilp300")
    public void solveIlpLarge() throws Exception {
        solve("ch.fhnw.ip6.ilpsolver.Solver", LARGE);
    }

    @GetMapping("opta300")
    public void solveOptaplannerLarge() throws Exception {
        solve("ch.fhnw.ip6.optasolver.Solver", LARGE);
    }

    @GetMapping("ortools")
    public void solveOrTools() throws Exception {
        solve("ch.fhnw.ip6.ortoolssolver.Solver", NORMAL);
    }

    @GetMapping("ilp")
    public void solveIlp() throws Exception {
        solve("ch.fhnw.ip6.ilpsolver.Solver", NORMAL);
    }

    @GetMapping("opta")
    public void solveOptaplanner() throws Exception {
        solve("ch.fhnw.ip6.optasolver.Solver", NORMAL);
    }


    @GetMapping("auto")
    public void auto() throws Exception {

        TestMode[] modes = {NORMAL, LARGE};
        String[] solvers = {"ch.fhnw.ip6.optasolver.Solver", "ch.fhnw.ip6.ilpsolver.Solver", "ch.fhnw.ip6.ortoolssolver.Solver"};

        Thread auto = new Thread(() -> Arrays.stream(modes).forEach(m -> {
            for (String s : solvers) {
                AtomicInteger run = new AtomicInteger(1);
                while(run.get() <= 5) {
                    log.info("Start of auto-solving {} mode {} run {}", s, m.getIndicator(), run);
                    solve(s, m);
                    try {
                        TimeUnit.SECONDS.sleep(10);

                        while (solverContext.isSolving()) {
                            TimeUnit.SECONDS.sleep(10);
                            log.debug("{} {} is still solving ({})", s, m.getIndicator(), solverContext.getPlanning() != null ? solverContext.getPlanning().getCost() : "nA");
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    run.getAndIncrement();
                }
            }
        }));
        auto.start();

    }


    private void solve(String solverName, TestMode testMode) {
        if (solverName == null) {
            throw new FachlicheException("Kein Solver angegeben.");
        }
        if (solverContext.isSolving()) {
            throw new FachlicheException("Es wird bereits eine Planung erstellt.");
        }
        applicationEventPublisher.publishEvent(new SolveEvent(this, solverName, testMode));
    }


}
