package ch.fhnw.ip6.ilpsolver.constraint.soft;

import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Timeslot;
import ch.fhnw.ip6.ilpsolver.constraint.Constraint;
import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBVar;

import static ch.fhnw.ip6.common.util.CostUtil.ROOM_SWITCH_COST;

/**
 * 1. Coaches sollen den Room möglichst selten wechseln.
 */
public class MinRoomSwitches extends Constraint {
    @Override
    public void build() {
        try {
            GRBVar[][] coachRoom = new GRBVar[getModel().getLecturers().size()][getModel().getRooms().size()];
            for (Lecturer l : getModel().getLecturers()) {
                for (Room r : getModel().getRooms()) {
                    coachRoom[l.getId()][r.getId()] = getGrbModel().addVar(0.0, 1.0, 1.0, GRB.EQUAL, l + "." + r);
                }
            }
            for (Lecturer l : getModel().getLecturers()) {
                for (Room r : getModel().getRooms()) {
                    GRBLinExpr lhs = new GRBLinExpr();
                    for (Presentation p1 : getModel().getPresentationsPerLecturer().get(l)) {
                        for (Timeslot t : getModel().getTimeslots()) {
                            if (getX()[p1.getId()][r.getId()][t.getId()] == null) continue;
                            lhs.addTerm(ROOM_SWITCH_COST, getX()[p1.getId()][r.getId()][t.getId()]);
                        }
                    }

                    getGrbModel().addGenConstrIndicator(coachRoom[indexOf(l)][indexOf(r)], 1, lhs, GRB.GREATER_EQUAL, 1.0, getConstraintName());
                    getGrbModel().addGenConstrIndicator(coachRoom[indexOf(l)][indexOf(r)], 0, lhs, GRB.LESS_EQUAL, 1.0, getConstraintName());


                }
            }
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getConstraintName() {
        return "MinRoomSwitches";
    }
}
