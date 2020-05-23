package ch.fhnw.ip6.originalsolver;

import ch.fhnw.ip6.api.SolverContext;

public class Program {

    public static void main(String[] args) {
        Solver solver = new Solver(new SolverContext());
        solver.testSolve();
    }
}
