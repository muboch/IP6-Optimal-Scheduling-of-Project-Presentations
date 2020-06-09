package ch.fhnw.ip6.optasolver;

import ch.fhnw.ip6.optasolver.constraints.CoachAndExpertSameConflictConstraint;
import ch.fhnw.ip6.optasolver.constraints.MinimizeRoomsConstraint;
import ch.fhnw.ip6.optasolver.constraints.MinimizeTimeslotsConstraint;
import ch.fhnw.ip6.optasolver.constraints.OptaConstraint;
import ch.fhnw.ip6.optasolver.constraints.RoomConflictConstraint;
import ch.fhnw.ip6.optasolver.constraints.RoomTypeConstraint;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class OptaConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        Set<OptaConstraint> constraints = new HashSet<>();

        // Hard Constraints
        constraints.add(new RoomConflictConstraint());
        constraints.add(new CoachAndExpertSameConflictConstraint());
        constraints.add(new RoomTypeConstraint());

        // Soft Constraints
        constraints.add(new MinimizeRoomsConstraint());
        constraints.add(new MinimizeTimeslotsConstraint());
        //constraints.add(new MinimizeTimeslotsConstraint());


        constraints.forEach(c -> c.setConstraintFactory(constraintFactory));
        return constraints.stream().map(OptaConstraint::build).toArray(Constraint[]::new);
    }
}