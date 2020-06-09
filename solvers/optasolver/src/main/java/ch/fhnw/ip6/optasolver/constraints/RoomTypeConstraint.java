package ch.fhnw.ip6.optasolver.constraints;

import ch.fhnw.ip6.common.dto.PresentationDto;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;

public class RoomTypeConstraint extends OptaConstraint{
    @Override
    public Constraint build() {
        return constraintFactory.from(PresentationDto.class).filter((pres) -> !pres.getType().equals(pres.getRoom().getType())).penalize("RoomType conflict", HardSoftScore.ONE_HARD);    }
}
