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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

import static ch.fhnw.ip6.ospp.event.SolveEvent.TestMode.*;

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
    public void solveOrToolsLarge(@RequestParam(required = false) Integer timeLimit) throws Exception {
        solve("ch.fhnw.ip6.ortoolssolver.Solver", timeLimit, LARGE);
    }

    @GetMapping("ilp300")
    public void solveIlpLarge(@RequestParam(required = false) Integer timeLimit) throws Exception {
        solve("ch.fhnw.ip6.ilpsolver.Solver", timeLimit, LARGE);
    }

    @GetMapping("opta300")
    public void solveOptaplannerLarge(@RequestParam(required = false) Integer timeLimit) throws Exception {
        solve("ch.fhnw.ip6.optasolver.Solver", timeLimit, LARGE);
    }

    @GetMapping("ortools")
    public void solveOrTools(@RequestParam(required = false) Integer timeLimit) throws Exception {
        solve("ch.fhnw.ip6.ortoolssolver.Solver", timeLimit, NORMAL);
    }

    @GetMapping("ilp")
    public void solveIlp(@RequestParam(required = false) Integer timeLimit) throws Exception {
        solve("ch.fhnw.ip6.ilpsolver.Solver", timeLimit, NORMAL);
    }

    @GetMapping("opta")
    public void solveOptaplanner(@RequestParam(required = false) Integer timeLimit) throws Exception {
        solve("ch.fhnw.ip6.optasolver.Solver", timeLimit, NORMAL);
    }

    private void solve(String solverName, Integer timeLimit, TestMode testMode) throws Exception {

    @GetMapping("auto")
    public void auto() throws Exception {

        TestMode[] modes = {NORMAL, LARGE};
        String[] solvers = {"ch.fhnw.ip6.optasolver.Solver", "ch.fhnw.ip6.ilpsolver.Solver", "ch.fhnw.ip6.ortoolssolver.Solver"};

        Arrays.stream(modes).forEach(m -> {
            AtomicInteger run = new AtomicInteger(1);
            for (String s : solvers) {
                log.info("Start of auto-solving {} mode {} run {}", s, m.getIndicator(), run);
                solve(s, m);
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                while (solverContext.isSolving())
                    log.debug("{} {} is still solving ({})", s, m.getIndicator(), solverContext.getPlanning() != null ? solverContext.getPlanning().getCost() : "nA");
        if (solverName == null) {
            throw new FachlicheException("Kein Solver angegeben.");
        }
        if (solverContext.isSolving()) {
            throw new FachlicheException("Es wird bereits eine Planung erstellt.");
        }
        applicationEventPublisher.publishEvent(new SolveEvent(this, solverName, testMode, timeLimit == null ? 360 : timeLimit));
    }


}
