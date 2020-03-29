package ch.fhnw.ip6.cpsolver;

public class Program {

    static {
        System.loadLibrary("jniortools");
    }
    public static void main(String[] args) {
        Solver solver = new Solver();
        solver.testSolve();
    }
}
