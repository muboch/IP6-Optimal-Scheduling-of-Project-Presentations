package ch.fhnw.ip6.ilpsolver.constraint.soft;

import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.ilpsolver.constraint.SoftConstraint;
import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBVar;

import static ch.fhnw.ip6.common.util.CostUtil.USED_ROOM_COST;

public class MinRoomUsages extends SoftConstraint {
    @Override
    public void build() {

        try {
            GRBVar[] roomUsed = new GRBVar[getIlpModel().getRooms().size()];

            for (R r : getIlpModel().getRooms()) {
                roomUsed[indexOf(r)] = getGrbModel().addVar(0, 1, 1.0, GRB.BINARY, r.toString());
            }

            for (R r : getIlpModel().getRooms()) {

                GRBLinExpr lhs = new GRBLinExpr();

                for (T t : getIlpModel().getTimeslots()) {
                    for (P p : getIlpModel().getPresentations()) {
                        if (getX()[indexOf(p)][indexOf(t)][indexOf(r)] == null) continue;
                        lhs.addTerm(1.0, getX()[indexOf(p)][indexOf(t)][indexOf(r)]);
                    }
                }
                // if lhs >= 1 then roomUsed
                // if lhs <= 0 then !roomUsed
                // At most N of A, B, C,...  a + b + c+. . . ≤ N
                // At least N of A, B, C,... a + b + c+. . . ≥ N
                // A - B <= M1*(1-P) - 1
                // B - A <= M2*P - 1
                //lhs.addTerm(-1.0, roomUsed[indexOf(r)]);
                getGrbModel().addGenConstrIndicator(roomUsed[indexOf(r)], 0, lhs, GRB.LESS_EQUAL, 0.0, "notUsed" + r.getName());
                getGrbModel().addGenConstrIndicator(roomUsed[indexOf(r)], 1, lhs, GRB.GREATER_EQUAL, 1.0, "used" + r.getName());
                getObjectives().addTerm(USED_ROOM_COST, roomUsed[indexOf(r)]);

            }
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getConstraintName() {
        return "MinRoomUsages";
    }
}
