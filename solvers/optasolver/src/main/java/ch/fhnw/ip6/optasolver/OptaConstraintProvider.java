package ch.fhnw.ip6.optasolver;

import ch.fhnw.ip6.optasolver.constraints.*;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

import java.util.HashSet;
import java.util.Set;

public class OptaConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        Set<OptaConstraint> constraints = new HashSet<>();

        // Hard Constraints
        constraints.add(new RoomConflictConstraint());
        constraints.add(new CoachAndExpertSameConflictConstraint());
        constraints.add(new RoomTypeConstraint());
        constraints.add(new OfftimesConstraint());


        // Soft Constraints
        constraints.add(new MinimizeRoomsConstraint());
        constraints.add(new MinimizeTimeslotsConstraint());
        constraints.add(new MinimizeFreeTimeslotsConstraint());
        constraints.add(new MinimizeRoomSwitchesConstraint());

        constraints.forEach(c -> c.setConstraintFactory(constraintFactory));
        return constraints.stream().map(OptaConstraint::build).toArray(Constraint[]::new);
    }
}