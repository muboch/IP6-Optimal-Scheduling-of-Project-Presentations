package ch.fhnw.ip6.ilpsolver;

import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.common.model.Model;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.List;

public class ILPModel extends Model<GRBModel, GRBVar> {

    public ILPModel(List<P> presentations, List<L> lecturers, List<R> rooms, List<T> timeslots, boolean[][] locktimes, GRBModel model) {
        super(presentations, lecturers, rooms, timeslots, locktimes, model);
    }

    @Override
    protected GRBVar[][][] setupVars() {
        return new GRBVar[0][][];
    }
}
