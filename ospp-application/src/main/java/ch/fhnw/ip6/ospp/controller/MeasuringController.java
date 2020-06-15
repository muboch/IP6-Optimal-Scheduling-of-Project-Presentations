package ch.fhnw.ip6.ospp.controller;


import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.ospp.service.FachlicheException;
import ch.fhnw.ip6.ospp.service.PlanningService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/measuring")
@RequiredArgsConstructor
@Slf4j
public class MeasuringController {

    private final PlanningService planningService;


    @GetMapping("ortools")
    public String solveOrTools(@RequestParam(required = false) Integer timeLimit) throws Exception {
        return solve("ch.fhnw.ip6.ortoolssolver.Solver", timeLimit);
    }

    @GetMapping("ilp")
    public String solveIlp(@RequestParam(required = false) Integer timeLimit) throws Exception {
        return solve("ch.fhnw.ip6.ilpsolver.Solver", timeLimit);
    }

    @GetMapping("opta")
    public String solveOptaplanner(@RequestParam(required = false) Integer timeLimit) throws Exception {
        return solve("ch.fhnw.ip6.optasolver.Solver", timeLimit);
    }

    private String solve(String solverName, Integer timeLimit) throws Exception {
        if (solverName == null) {
            throw new FachlicheException("Kein Solver angegeben.");
        }
        planningService.setSolverName(solverName);
        planningService.setTestMode(true);
        planningService.setTimeLimit(timeLimit == null ? 360 : timeLimit);
        Planning planning = planningService.plan();
        String log = planning.getPlanningStats();
        log += "\n" + planning.getPlanningAsTable();
        return log;
    }


}
