package ch.fhnw.ip6.optasolver;

import ch.fhnw.ip6.common.dto.PresentationDto;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class TimeTableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                // Hard constraints
                roomConflict(constraintFactory),
                coachConflict(constraintFactory),
                expertConflict(constraintFactory),

                // Soft constraints
        };
    }

    private Constraint roomConflict(ConstraintFactory constraintFactory) {
        // A room can accommodate at most one lesson at the same time.

        // Select a lesson ...
        return constraintFactory.from(PresentationDto.class)
                // ... and pair it with another lesson ...
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

    private Constraint coachConflict(ConstraintFactory constraintFactory) {
        // A coach can attend at most one lesson at the same time.
        return constraintFactory.from(PresentationDto.class)
                .join(PresentationDto.class,
                        Joiners.equal(PresentationDto::getTimeslot),
                        Joiners.equal(PresentationDto::getCoach),
                        Joiners.lessThan(PresentationDto::getId))
                .penalize("Coach conflict", HardSoftScore.ONE_HARD);
    }

    private Constraint expertConflict(ConstraintFactory constraintFactory) {
        // An expert can attend at most one lesson at the same time.
        return constraintFactory.from(PresentationDto.class)
                .join(PresentationDto.class,
                        Joiners.equal(PresentationDto::getTimeslot),
                        Joiners.equal(PresentationDto::getExpert),
                        Joiners.lessThan(PresentationDto::getId))
                .penalize("Expert conflict", HardSoftScore.ONE_HARD);
    }

}