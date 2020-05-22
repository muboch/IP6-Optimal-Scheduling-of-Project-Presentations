package ch.fhnw.ip6.originalsolver.constraint.soft;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;

/**
 * 1. Coaches sollen den Room möglichst selten wechseln.
 */
public class MinFreeTimeslots {
    public Constraint build(ConstraintFactory cf) {
        return null;
    }

    protected String getConstraintName() {
        return "MinRoomSwitches";
    }
}
