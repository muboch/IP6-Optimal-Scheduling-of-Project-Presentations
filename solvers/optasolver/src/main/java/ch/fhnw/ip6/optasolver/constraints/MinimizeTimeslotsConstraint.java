package ch.fhnw.ip6.optasolver.constraints;

import ch.fhnw.ip6.common.dto.PresentationDto;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;

public class MinimizeTimeslotsConstraint extends OptaConstraint{

    @Override
    public Constraint build() {
        return constraintFactory.from(PresentationDto.class).groupBy(PresentationDto::getTimeslot).penalize("minimize Timeslots", HardSoftScore.ONE_SOFT);
    }
}
