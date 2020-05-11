package ch.fhnw.ip6.ilpsolver.constraint.hard;

import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.common.dto.RoomDto;
import ch.fhnw.ip6.common.dto.TimeslotDto;
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
            for (LecturerDto l : getModel().getLecturers()) {
                for (TimeslotDto t : getModel().getTimeslots()) {
                    GRBLinExpr lhs = new GRBLinExpr();
                    for (RoomDto r : getModel().getRooms()) {
                        for (PresentationDto p : getModel().getPresentationsPerLecturer().get(l)) {
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