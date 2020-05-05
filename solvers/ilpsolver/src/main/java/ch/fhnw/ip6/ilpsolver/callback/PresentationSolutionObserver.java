package ch.fhnw.ip6.ilpsolver.callback;

import gurobi.GRB;
import gurobi.GRBCallback;
import gurobi.GRBModel;
import gurobi.GRBVar;
import lombok.SneakyThrows;

public class PresentationSolutionObserver extends GRBCallback {

    private final GRBVar[][][] x;
    private final int nPresentations;
    private final int nTimeslots;
    private final int nRooms;
    private final GRBModel model;

    @SneakyThrows
    @Override
    protected void callback() {

        if (where == GRB.CB_MIP) {

            for (int p = 0; p < nPresentations; p++) {
                for (int t = 0; t < nTimeslots; t++) {
                    for (int r = 0; r < nRooms; r++) {
                        if (x[p][t][r].get(GRB.DoubleAttr.X) == 1.0) {
                            System.out.println(x[p][t][r].get(GRB.StringAttr.VarName) + " " + 1.0);
                        }
                    }
                }
            }
        }
    }

    public PresentationSolutionObserver(GRBVar[][][] x, int nPresentations, int nTimeslots, int nRooms, GRBModel model) {
        this.x = x;
        this.nPresentations = nPresentations;
        this.nTimeslots = nTimeslots;
        this.nRooms = nRooms;
        this.model = model;
    }
}
