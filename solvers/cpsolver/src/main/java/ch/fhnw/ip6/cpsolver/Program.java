package ch.fhnw.ip6.cpsolver;

import ch.fhnw.ip6.api.SolverContext;

public class Program {

    static {
        System.loadLibrary("jniortools");
    }
    public static void main(String[] args) {
        Solver solver = new Solver(new SolverContext());
        solver.testSolve();
    }
}
