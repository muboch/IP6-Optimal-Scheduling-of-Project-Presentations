package ch.fhnw.ip6.optasolver;

import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.optasolver.mapper.OptaMapperImpl;

public class Program {

    public static void main(String[] args) {
        Solver solver = new Solver(new SolverContext());
        solver.setMapper(new OptaMapperImpl());
        solver.testSolve();
    }
}
