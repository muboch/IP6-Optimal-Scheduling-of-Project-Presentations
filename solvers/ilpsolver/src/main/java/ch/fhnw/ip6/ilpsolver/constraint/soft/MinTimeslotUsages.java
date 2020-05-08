package ch.fhnw.ip6.ilpsolver.constraint.soft;

import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Timeslot;
import ch.fhnw.ip6.common.util.CostUtil;
import ch.fhnw.ip6.ilpsolver.constraint.SoftConstraint;
import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBVar;

import static ch.fhnw.ip6.common.util.CostUtil.USED_ROOM_COST;

public class MinTimeslotUsages extends SoftConstraint {
    @Override
    public void build() {

        try {
            GRBVar[] timeslotUsed = new GRBVar[getIlpModel().getTimeslots().size()];
            for (Timeslot t : getIlpModel().getTimeslots()) {
                timeslotUsed[indexOf(t)] = getGrbModel().addVar(0, 1, 1.0, GRB.BINARY, t.toString());
            }

            for (Timeslot t : getIlpModel().getTimeslots()) {
                GRBLinExpr lhs = new GRBLinExpr();
                for (Room r : getIlpModel().getRooms()) {
                    for (Presentation p : getIlpModel().getPresentations()) {
                        lhs.addTerm(t.getPriority(), getX()[indexOf(p)][indexOf(t)][indexOf(r)]);
                    }
                }
                getGrbModel().addGenConstrIndicator(timeslotUsed[indexOf(t)], 0, lhs, GRB.LESS_EQUAL, 0.0, "notUsed" + t.getDate());
                getGrbModel().addGenConstrIndicator(timeslotUsed[indexOf(t)], 1, lhs, GRB.GREATER_EQUAL, 1.0, "used" + t.getDate());
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
