package ch.fhnw.ip6.ilpsolver.constraint.hard;

import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Timeslot;
import ch.fhnw.ip6.ilpsolver.constraint.Constraint;
import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBLinExpr;

/**
 * 1. Ein Coach kann während eines Timeslots höchstens eine Presentation besuchen.
 * 2. Ein Expert kann während eines Timeslots höchstens eine Presentation besuchen.
 */
public class LecturerNotMoreThanOnePresentationPerTimeslot extends Constraint {

    @Override
    public void build() {
        try {
            for (Lecturer l : getIlpModel().getLecturers()) {
                for (Timeslot t : getIlpModel().getTimeslots()) {
                    GRBLinExpr lhs = new GRBLinExpr();
                    for (Room r : getIlpModel().getRooms()) {
                        for (Presentation p : getIlpModel().getPresentationsPerLecturer().get(l)) {
                            lhs.addTerm(1.0, getX()[indexOf(p)][indexOf(t)][indexOf(r)]);
                        }
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
        return "LecturerNotMoreThanOnePresentationPerTimeslot";
    }
}
