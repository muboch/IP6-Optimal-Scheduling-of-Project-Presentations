package ch.fhnw.ip6.originalsolver.constraint.hard;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;

/**
 * 3. Alle Presentations müssen stattfinden.
 */
public class AllPresentationsToRoomAndTimeslotAssigned {

    public Constraint build(ConstraintFactory cf) {
        return null;
    }

    protected String getConstraintName() {
        return "AllPresentationsToRoomAndTimeslotAssigned";
    }
}
