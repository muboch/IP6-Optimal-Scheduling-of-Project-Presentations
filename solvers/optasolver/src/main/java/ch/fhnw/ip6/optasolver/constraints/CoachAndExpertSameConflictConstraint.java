package ch.fhnw.ip6.optasolver.constraints;

import ch.fhnw.ip6.common.dto.PresentationDto;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.Joiners;

public class CoachAndExpertSameConflictConstraint extends OptaConstraint {

    @Override
    public Constraint build() {
        // A lecturer can attend at most one lesson at the same time.
        // get presentation
        return constraintFactory.from(PresentationDto.class)
                // pair with another presentation
                .join(PresentationDto.class,
                        // in the same timeslot
                        Joiners.equal(PresentationDto::getTimeslot))
                // filter if coach and expert are the same somewhere
                .filter((p1, p2) -> p1.getExpert() == p2.getExpert() || p1.getCoach() == p2.getCoach() || p1.getExpert() == p2.getCoach() || p1.getCoach() == p2.getExpert()).penalize("Expert/Coach conflict", HardSoftScore.ONE_HARD);
    }
}
