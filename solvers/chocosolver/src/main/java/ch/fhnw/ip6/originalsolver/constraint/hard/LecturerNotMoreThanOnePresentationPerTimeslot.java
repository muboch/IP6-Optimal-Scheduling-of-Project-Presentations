package ch.fhnw.ip6.originalsolver.constraint.hard;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;

/**
 * 1. Ein Coach kann während eines Timeslots höchstens eine Presentation besuchen.
 * 2. Ein Expert kann während eines Timeslots höchstens eine Presentation besuchen.
 */
public class LecturerNotMoreThanOnePresentationPerTimeslot {

    public Constraint build(ConstraintFactory cf) {
        return null;
    }

    protected String getConstraintName() {
        return "LecturerNotMoreThanOnePresentationPerTimeslot";
    }
}
