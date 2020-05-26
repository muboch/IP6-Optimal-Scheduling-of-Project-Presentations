package ch.fhnw.ip6.ilpsolver.constraint.soft;

import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.ilpsolver.constraint.SoftConstraint;
import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBVar;

public class MinTimeslotUsages extends SoftConstraint {
    @Override
    public void build() {

        try {
            GRBVar[] timeslotUsed = new GRBVar[getIlpModel().getTimeslots().size()];
            for (T t : getIlpModel().getTimeslots()) {
                timeslotUsed[indexOf(t)] = getGrbModel().addVar(0, 1, 0.0, GRB.BINARY, "t-" + t.getId());
            }

            for (T t : getIlpModel().getTimeslots()) {
                GRBLinExpr lhs = new GRBLinExpr();
                for (R r : getIlpModel().getRooms()) {
                    for (P p : getIlpModel().getPresentations()) {
                        lhs.addTerm(t.getPriority(), getX()[indexOf(p)][indexOf(t)][indexOf(r)]);
                    }
                }
                getGrbModel().addGenConstrIndicator(timeslotUsed[indexOf(t)], 0, lhs, GRB.LESS_EQUAL, 0.0, "notUsed" + t.getId());
                getGrbModel().addGenConstrIndicator(timeslotUsed[indexOf(t)], 1, lhs, GRB.GREATER_EQUAL, 1.0, "used" + t.getId());
                getObjectives().addTerm(t.getPriority(), timeslotUsed[indexOf(t)]);
            }
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getConstraintName() {
        return "MinTimeslotUsages";
    }
}
