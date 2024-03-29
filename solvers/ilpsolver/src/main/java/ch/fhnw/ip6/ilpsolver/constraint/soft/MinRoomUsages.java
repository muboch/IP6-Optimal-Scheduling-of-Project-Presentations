package ch.fhnw.ip6.ilpsolver.constraint.soft;

import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.common.util.CostUtil;
import ch.fhnw.ip6.ilpsolver.constraint.SoftConstraint;
import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBVar;

import static ch.fhnw.ip6.common.util.CostUtil.USED_ROOM_COST;

public class MinRoomUsages extends SoftConstraint {
    @Override
    public void build() {

        // final double MAX_ROOMS = (1.0 / getIlpModel().getPresentationsPerLecturer().values().stream().max(Comparator.comparingInt(List::size)).get().size());
        final double MAX_ROOMS = (1.0 / getIlpModel().getRooms().size());

        try {
            for (R r : getIlpModel().getRooms()) {

                GRBLinExpr linExpr = new GRBLinExpr();

                GRBVar roomUsed = getGrbModel().addVar(0, 1, 0.0, GRB.BINARY, null);

                GRBLinExpr sumOfUsedRooms = new GRBLinExpr();
                for (T t : getIlpModel().getTimeslots()) {
                    for (P p : getIlpModel().getPresentations()) {
                        if (getX()[indexOf(p)][indexOf(t)][indexOf(r)] != null) {
                            sumOfUsedRooms.addTerm(MAX_ROOMS, getX()[indexOf(p)][indexOf(t)][indexOf(r)]);
                            linExpr.addTerm(1.0, getX()[indexOf(p)][indexOf(t)][indexOf(r)]);
                        }
                    }
                }

                getGrbModel().addConstr(roomUsed, GRB.GREATER_EQUAL, sumOfUsedRooms, null);

                getObjectives().addTerm(USED_ROOM_COST, roomUsed);

            }

            // subtract cost for each lecturer with min. one presentation. this is because the first usage of a room (entrance) is counted. this is actually false as this is no room switch
            getObjectives().addConstant(-(int) getIlpModel().getPresentationsPerLecturer().entrySet().stream().filter(e -> e.getValue().size() > 0).count() * CostUtil.ROOM_SWITCH_COST);

        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getConstraintName() {
        return "MinRoomUsages";
    }
}
