package ch.fhnw.ip6.optasolver.constraints;

import ch.fhnw.ip6.optasolver.model.Lecturer;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;

import static ch.fhnw.ip6.common.util.CostUtil.ROOM_SWITCH_COST;

public class MinimizeRoomSwitchesConstraint extends OptaConstraint {

    @Override
    public Constraint build() {
        return constraintFactory.from(Lecturer.class)
                .penalize("Room Switches",
                        HardSoftScore.ONE_SOFT, (l) -> {
                            if (l.getRoomSwitches() == null) {
                                return 999;
                            }
                            return l.getRoomSwitches() * ROOM_SWITCH_COST;
                        });

    }
}
