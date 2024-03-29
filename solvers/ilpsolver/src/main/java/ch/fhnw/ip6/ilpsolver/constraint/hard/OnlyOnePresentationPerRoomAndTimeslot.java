package ch.fhnw.ip6.ilpsolver.constraint.hard;

import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.ilpsolver.constraint.Constraint;
import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBLinExpr;

/**
 * 4. Alle Presentations müssen genau einem Timeslot und genau einem Room zugewiesen werden.
 */
public class OnlyOnePresentationPerRoomAndTimeslot extends Constraint {

    @Override
    public void build() {
        try {
            for (R r : getIlpModel().getRooms()) {
                for (T t : getIlpModel().getTimeslots()) {

                    GRBLinExpr lhs = new GRBLinExpr();
                    for (P p : getIlpModel().getPresentations()) {
                        if (getX()[indexOf(p)][indexOf(t)][indexOf(r)] != null)
                            lhs.addTerm(1.0, getX()[indexOf(p)][indexOf(t)][indexOf(r)]);
                    }
                    getGrbModel().addConstr(lhs, GRB.LESS_EQUAL, 1.0, getConstraintName());
                }

            }
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getConstraintName() {
        return "OnlyOnePresentationPerRoomAndTimeslot";
    }
}
