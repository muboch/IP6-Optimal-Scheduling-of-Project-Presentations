package ch.fhnw.ip6.optasolver.constraints;

import ch.fhnw.ip6.optasolver.model.Presentation;
import ch.fhnw.ip6.optasolver.model.Room;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.Joiners;

import static ch.fhnw.ip6.common.util.CostUtil.USED_ROOM_COST;

public class MinimizeRoomsConstraint extends OptaConstraint {

    @Override
    public Constraint build() {

        return constraintFactory.from(Room.class).penalize("minimize Rooms", HardSoftScore.ONE_SOFT, r -> {
            if(r.getPresentationList().size() > 0){
                return USED_ROOM_COST;
            } else {
                return 0;
            }
        });
    }
}
