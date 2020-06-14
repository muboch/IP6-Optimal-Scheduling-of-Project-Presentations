package ch.fhnw.ip6.ilpsolver;

import ch.fhnw.ip6.api.SolverContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Program {

    public static void main(String[] args) {
        SolverContext sc = new SolverContext();
        LocalDateTime now = LocalDateTime.now();
        sc.setLogFileName("ilp-solver-" + now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
        Solver solver = new Solver(sc);
        solver.testSolve();
    }
}
