package ch.fhnw.ip6.ilpsolver.constraint.hard;

import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.ilpsolver.constraint.Constraint;
import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBLinExpr;

/**
 * 3. Alle Presentations müssen stattfinden.
 */
public class AllPresentationsToRoomAndTimeslotAssigned extends Constraint {

    @Override
    public void build() {

        try {
            for (P p : getIlpModel().getPresentations()) {
                GRBLinExpr lhs = new GRBLinExpr();
                for (T t : getIlpModel().getTimeslots()) {
                    for (R r : getIlpModel().getRooms()) {
                        // 9. Eine Presentation kann nur in einem Room vom passenden RoomType stattfinden.
                        if (r.getType().equals(p.getType()))
                            lhs.addTerm(1.0, getX()[indexOf(p)][indexOf(t)][indexOf(r)]);
                    }
                }
                addConstraint(lhs, GRB.EQUAL);
            }
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getConstraintName() {
        return "AllPresentationsToRoomAndTimeslotAssigned";
    }
}
