package ch.fhnw.ip6.ilpsolver.constraint.hard;

import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Timeslot;
import ch.fhnw.ip6.ilpsolver.constraint.Constraint;
import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBLinExpr;

public class OnlyOnePresentationPerRoomAndTimeslot extends Constraint {

    @Override
    public void build() {
        try {
            for (Timeslot t : getModel().getTimeslots()) {
                for (Room r : getModel().getRooms()) {
                    GRBLinExpr lhs = new GRBLinExpr();
                    for (Presentation p : getModel().getPresentations()) {
                        lhs.addTerm(1.0, getX()[indexOf(p)][indexOf(t)][indexOf(r)]);
                    }
                    addConstraint(lhs, GRB.LESS_EQUAL);
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
