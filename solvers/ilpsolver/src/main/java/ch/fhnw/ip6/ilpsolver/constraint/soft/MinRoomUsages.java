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

        //final double MAX_ROOMS = (double) Math.round((1.0 / getIlpModel().getRooms().size()) * 10) / 10;

        try {
            for (R r : getIlpModel().getRooms()) {

                GRBVar roomUsed = getGrbModel().addVar(0, 1, 0.0, GRB.BINARY, null);

                GRBLinExpr sumOfUsedRooms = new GRBLinExpr();
                for (T t : getIlpModel().getTimeslots()) {
                    for (P p : getIlpModel().getPresentations()) {
                        if (getX()[indexOf(p)][indexOf(t)][indexOf(r)] != null) {
                            sumOfUsedRooms.addTerm(1.0, getX()[indexOf(p)][indexOf(t)][indexOf(r)]);
                        }
                    }
                }
                getGrbModel().addGenConstrIndicator(roomUsed, 1, sumOfUsedRooms, GRB.GREATER_EQUAL, 1.0, null);
                getGrbModel().addGenConstrIndicator(roomUsed, 0, sumOfUsedRooms, GRB.LESS_EQUAL, 1.0, null);
                getObjectives().addTerm(USED_ROOM_COST, roomUsed);

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
