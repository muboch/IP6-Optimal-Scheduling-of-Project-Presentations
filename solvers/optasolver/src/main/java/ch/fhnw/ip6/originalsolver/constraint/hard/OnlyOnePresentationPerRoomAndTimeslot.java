package ch.fhnw.ip6.originalsolver.constraint.hard;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;

/**
 * 4. Alle Presentations müssen genau einem Timeslot und genau einem Room zugewiesen werden.
 */
public class OnlyOnePresentationPerRoomAndTimeslot {

    public Constraint build(ConstraintFactory cf) {
        return null;
    }

    protected String getConstraintName() {
        return "OnlyOnePresentationPerRoomAndTimeslot";
    }
}
