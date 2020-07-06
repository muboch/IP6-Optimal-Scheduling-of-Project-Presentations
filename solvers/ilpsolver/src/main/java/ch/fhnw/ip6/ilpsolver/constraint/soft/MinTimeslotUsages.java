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


            for (T t : getIlpModel().getTimeslots()) {

                GRBVar timeslotUsed = getGrbModel().addVar(0, 1, 0.0, GRB.BINARY, null);

                GRBLinExpr sumOfUsedTimeslots = new GRBLinExpr();
                for (R r : getIlpModel().getRooms()) {
                    for (P p : getIlpModel().getPresentations()) {
                        if (getX()[indexOf(p)][indexOf(t)][indexOf(r)] != null) {
                            sumOfUsedTimeslots.addTerm(1.0, getX()[indexOf(p)][indexOf(t)][indexOf(r)]);
                        }
                    }
                }
                getGrbModel().addGenConstrIndicator(timeslotUsed, 1, sumOfUsedTimeslots, GRB.GREATER_EQUAL, 1.0, null);
                getGrbModel().addGenConstrIndicator(timeslotUsed, 0, sumOfUsedTimeslots, GRB.LESS_EQUAL, 1.0, null);

                getObjectives().addTerm(t.getPriority(), timeslotUsed);

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
