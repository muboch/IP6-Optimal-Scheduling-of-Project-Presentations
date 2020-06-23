package ch.fhnw.ip6.ilpsolver.constraint.hard;

import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.ilpsolver.constraint.Constraint;
import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBLinExpr;

/**
 * 1. Ein Lecuter kann während eines Timeslots höchstens eine Presentation besuchen.
 */
public class LecturerNotMoreThanOnePresentationPerTimeslot extends Constraint {

    @Override
    public void build() {
        try {
            for (L l : getIlpModel().getLecturers()) {
                for (T t : getIlpModel().getTimeslots()) {
                    GRBLinExpr lhs = new GRBLinExpr();
                    for (R r : getIlpModel().getRooms()) {
                        for (P p : getIlpModel().getPresentationsPerLecturer().get(l)) {
                            if (getX()[indexOf(p)][indexOf(t)][indexOf(r)] != null)
                                lhs.addTerm(1.0, getX()[indexOf(p)][indexOf(t)][indexOf(r)]);
                        }
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
        return "LecturerNotMoreThanOnePresentationPerTimeslot";
    }
}
