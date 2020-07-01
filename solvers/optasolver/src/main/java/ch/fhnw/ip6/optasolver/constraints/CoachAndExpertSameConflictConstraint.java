package ch.fhnw.ip6.optasolver.constraints;

import ch.fhnw.ip6.optasolver.model.Presentation;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.Joiners;

public class CoachAndExpertSameConflictConstraint extends OptaConstraint {

    @Override
    public Constraint build() {
        // A lecturer can attend at most one lesson at the same time.
        // get presentation
        return constraintFactory.from(Presentation.class)
                // pair with another presentation
                .join(Presentation.class,
                        // Where NR is not same (dont compare presentation with each other)
                        Joiners.lessThan(Presentation::getId),
                        // in the same timeslot
                        Joiners.equal(Presentation::getTimeslot))
                // filter if coach and expert are the same somewhere
                .filter((p1, p2) -> p1.getExpert().equals(p2.getExpert()) || p1.getCoach().equals(p2.getCoach()) || p1.getExpert().equals(p2.getCoach()) || p1.getCoach().equals(p2.getExpert())).penalize("Expert/Coach conflict", HardSoftScore.ONE_HARD);
    }
}
