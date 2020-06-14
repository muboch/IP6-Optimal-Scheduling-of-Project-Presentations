package ch.fhnw.ip6.optasolver.constraints;

import ch.fhnw.ip6.optasolver.model.Lecturer;
import ch.fhnw.ip6.optasolver.model.Presentation;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.Joiners;


public class MinimizeFreeTimeslotsConstraint extends OptaConstraint {
    @Override
    public Constraint build() {

        // Select a lecturer
        return constraintFactory.from(Presentation.class)
                .join(Lecturer.class,
                        Joiners.equal(Presentation::getCoachInitials, Lecturer::getInitials))
                .penalize("Minimize Free Timeslots",
                        HardSoftScore.ONE_HARD);

    }

}
