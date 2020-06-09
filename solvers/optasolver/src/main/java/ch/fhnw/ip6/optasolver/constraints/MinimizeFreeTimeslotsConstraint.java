package ch.fhnw.ip6.optasolver.constraints;

import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.ospp.model.Lecturer;
import org.hibernate.mapping.Join;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.Joiners;


public class MinimizeFreeTimeslotsConstraint extends OptaConstraint{
    @Override
    public Constraint build() {

        // Select a lecturer
        return constraintFactory.from(PresentationDto.class)
        .join(LecturerDto.class,
                        Joiners.equal(PresentationDto::getCoachInitials, LecturerDto::getInitials))

                .penalize("Shift on an off-day",
                        HardSoftScore.ONE_HARD);

    }

}
