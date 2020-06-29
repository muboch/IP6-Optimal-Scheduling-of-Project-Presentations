package ch.fhnw.ip6.optasolver.constraints;

import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;

public abstract class OptaConstraint {

    protected ConstraintFactory constraintFactory;

    public void setConstraintFactory(ConstraintFactory constraintFactory) {
        this.constraintFactory = constraintFactory;
    }

    public abstract Constraint build();
}
