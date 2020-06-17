package ch.fhnw.ip6.ospp.controller;


import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.ospp.event.SolveEvent;
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


    @GetMapping("ortools")
    public void solveOrTools(@RequestParam(required = false) Integer timeLimit) throws Exception {
        solve("ch.fhnw.ip6.ortoolssolver.Solver", timeLimit);
    }

    @GetMapping("ilp")
    public void solveIlp(@RequestParam(required = false) Integer timeLimit) throws Exception {
        solve("ch.fhnw.ip6.ilpsolver.Solver", timeLimit);
    }

    @GetMapping("opta")
    public void solveOptaplanner(@RequestParam(required = false) Integer timeLimit) throws Exception {
        solve("ch.fhnw.ip6.optasolver.Solver", timeLimit);
    }

    private void solve(String solverName, Integer timeLimit) throws Exception {
        if (solverName == null) {
            throw new FachlicheException("Kein Solver angegeben.");
        }
        if (solverContext.isSolving()) {
            throw new FachlicheException("Es wird bereits eine Planung erstellt.");
        }
        applicationEventPublisher.publishEvent(new SolveEvent(this, solverName, true, timeLimit == null ? 360 : timeLimit));
    }


}
