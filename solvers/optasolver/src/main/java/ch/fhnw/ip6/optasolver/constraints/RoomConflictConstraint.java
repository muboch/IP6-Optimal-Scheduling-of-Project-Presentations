package ch.fhnw.ip6.optasolver.constraints;

import ch.fhnw.ip6.common.dto.PresentationDto;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.Joiners;

/**
 * A presentation can only be scheduled in one room at one time.
 */
public class RoomConflictConstraint extends OptaConstraint{

    @Override
    public Constraint build() {

        // Select a lesson ...
        return constraintFactory.from(PresentationDto.class)
                // ... and pair it with another presentation ...
                .join(PresentationDto.class,
                        // ... in the same timeslot ...
                        Joiners.equal(PresentationDto::getTimeslot),
                        // ... in the same room ...
                        Joiners.equal(PresentationDto::getRoom),
                        // ... and the pair is unique (different id, no reverse pairs)
                        Joiners.lessThan(PresentationDto::getId))
                // then penalize each pair with a hard weight.
                .penalize("Room conflict", HardSoftScore.ONE_HARD);
    }
}
