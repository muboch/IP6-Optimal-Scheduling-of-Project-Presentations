package ch.fhnw.ip6.optasolver;

import ch.fhnw.ip6.api.SolverContext;

public class Program {

    public static void main(String[] args) {
        Solver solver = new Solver(new SolverContext());
        solver.testSolve();
    }
}
