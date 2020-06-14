package ch.fhnw.ip6.optasolver.constraints;

import ch.fhnw.ip6.optasolver.model.Presentation;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;

import static ch.fhnw.ip6.common.util.CostUtil.USED_ROOM_COST;

public class MinimizeRoomsConstraint extends OptaConstraint {

    @Override
    public Constraint build() {
        return constraintFactory.from(Presentation.class).groupBy(Presentation::getRoom).penalize("minimize Rooms", HardSoftScore.ONE_SOFT, r -> USED_ROOM_COST);
    }
}
