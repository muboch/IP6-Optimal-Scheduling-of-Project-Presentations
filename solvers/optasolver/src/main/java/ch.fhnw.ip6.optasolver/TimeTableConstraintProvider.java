package ch.fhnw.ip6.optasolver;

import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.ospp.model.Lecturer;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class TimeTableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                // Hard constraints
                roomConflict(constraintFactory),
                //coachConflict(constraintFactory),
                //expertConflict(constraintFactory),
                coachAndExpertSameConflict(constraintFactory)

                // Soft constraints
        };
    }

    private Constraint roomConflict(ConstraintFactory constraintFactory) {
        // A room can accommodate at most one lesson at the same time.

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


    private Constraint coachAndExpertSameConflict(ConstraintFactory constraintFactory) {
        // A lecturer can attend at most one lesson at the same time.
        // get presentation
        return constraintFactory.from(PresentationDto.class)
                // pair with another presentation
                .join(PresentationDto.class,
                        // in the same timeslot
                        Joiners.equal(PresentationDto::getTimeslot))
                // filter if coach and expert are the same somewhere
                .filter((p1, p2) -> {
                    return p1.getExpert() == p2.getExpert() || p1.getCoach() == p2.getCoach() || p1.getExpert() == p2.getCoach() || p1.getCoach() == p2.getExpert();
                }).penalize("Expert/Coach conflict", HardSoftScore.ONE_HARD); // penalize with 1 hard constraint
    }
}