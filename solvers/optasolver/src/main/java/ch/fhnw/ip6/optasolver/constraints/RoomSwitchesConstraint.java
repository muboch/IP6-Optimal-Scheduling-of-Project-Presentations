package ch.fhnw.ip6.optasolver.constraints;

import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.RoomDto;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintCollectors;

public class RoomSwitchesConstraint extends OptaConstraint {

    @Override
    public Constraint build() {
        return constraintFactory.from(LecturerDto.class)
                .join(RoomDto.class)
                .groupBy((lecturerDto, r) -> ConstraintCollectors.countDistinct(RoomDto::getId))
                .penalize("Constraint name", HardSoftScore.ONE_SOFT);
    }
}
