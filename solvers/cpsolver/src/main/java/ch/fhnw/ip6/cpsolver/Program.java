package ch.fhnw.ip6.cpsolver;

import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.util.NativeUtils;

import java.io.IOException;

public class Program {
    {
        try {
            NativeUtils.loadLibraryFromJar("/resources/lib/libjniortools.so");
            NativeUtils.loadLibraryFromJar("/resources/lib/libortools.so");
        } catch (IOException e) {
            // This is probably not the best way to handle exception :-)
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Solver solver = new Solver(new SolverContext());
        solver.testSolve();

    }
}
