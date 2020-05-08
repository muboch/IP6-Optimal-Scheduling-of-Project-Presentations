package ch.fhnw.ip6.ilpsolver;

import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.common.model.Model;
import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.List;

public class ILPModel extends Model<GRBModel, GRBVar> {

    public ILPModel(List<P> presentations, List<L> lecturers, List<R> rooms, List<T> timeslots, boolean[][] locktimes, GRBModel model) {
        super(presentations, lecturers, rooms, timeslots, locktimes, model);
    }

    @Override
    protected GRBVar[][][] setupVars() {
        try {
            GRBVar[][][] X = new GRBVar[getPresentations().size()][getTimeslots().size()][getRooms().size()];
            for (int p = 0; p < getPresentations().size(); ++p) {
                for (int t = 0; t < getTimeslots().size(); ++t) {
                    for (int r = 0; r < getRooms().size(); ++r) {
                        X[p][t][r] = getModel().addVar(0, 1, 1.0, GRB.BINARY, getPresentations().get(p) + "." + getTimeslots().get(t) + "." + getRooms().get(r));
                    }
                }
            }
            return X;
        } catch (GRBException e) {
            e.printStackTrace();
        }
        return null;
    }
}
