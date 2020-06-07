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
            final double MAX_TIMESLOTS = 1.0 / getIlpModel().getTimeslots().size();

            for (T t : getIlpModel().getTimeslots()) {

                GRBLinExpr linExpr = new GRBLinExpr();

                GRBVar timeslotUsed = getGrbModel().addVar(0, 1, 0.0, GRB.BINARY, null);

                GRBLinExpr sumOfUsedTimeslots = new GRBLinExpr();
                for (R r : getIlpModel().getRooms()) {
                    for (P p : getIlpModel().getPresentations()) {
                        sumOfUsedTimeslots.addTerm(MAX_TIMESLOTS, getX()[indexOf(p)][indexOf(t)][indexOf(r)]);
                        linExpr.addTerm(1.0, getX()[indexOf(p)][indexOf(t)][indexOf(r)]);
                    }
                }

                getGrbModel().addConstr(timeslotUsed, GRB.GREATER_EQUAL, sumOfUsedTimeslots, null);

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
