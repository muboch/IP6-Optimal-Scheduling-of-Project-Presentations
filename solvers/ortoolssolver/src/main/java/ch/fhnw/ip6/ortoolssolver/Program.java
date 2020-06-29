package ch.fhnw.ip6.ortoolssolver;

import ch.fhnw.ip6.api.SolverContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Program {


    public static void main(String[] args) {
        SolverContext sc = new SolverContext();
        Solver solver = new Solver(sc);
        solver.testSolve();
    }
}
