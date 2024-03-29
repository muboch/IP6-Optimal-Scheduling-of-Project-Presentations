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

    public ILPModel(List<? extends P> presentations, List<? extends L> lecturers, List<? extends R> rooms, List<? extends T> timeslots, boolean[][] offTimes, GRBModel model) {
        super(presentations, lecturers, rooms, timeslots, offTimes, model);
    }

    @Override
    protected GRBVar[][][] setupVars() {
        try {
            GRBVar[][][] X = new GRBVar[getPresentations().size()][getTimeslots().size()][getRooms().size()];
            for (int p = 0; p < getPresentations().size(); ++p) {
                for (int t = 0; t < getTimeslots().size(); ++t) {
                    for (int r = 0; r < getRooms().size(); ++r) {
                        // no var if coach or expert has an offtime at this timeslote
                        //if (getOfftimes()[indexOf(getPresentations().get(p).getCoach())][t] || getOfftimes()[indexOf(getPresentations().get(p).getExpert())][t]) {
                        //    continue;
                        //}
                        if (getPresentations().get(p).getType().equals(getRooms().get(r).getType()))
                            X[p][t][r] = getModel().addVar(0, 1, 1.0, GRB.BINARY, "presTimeRoom" + getPresentations().get(p).getId() + "." + getTimeslots().get(t).getId() + "." + getRooms().get(r).getName());

                    }
                }
            }
            return X;
        } catch (GRBException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected GRBVar[][] setupVars2d() {
        return new GRBVar[0][];
    }
}
