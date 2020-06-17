package ch.fhnw.ip6.optasolver.constraints;

import ch.fhnw.ip6.optasolver.model.Presentation;
import ch.fhnw.ip6.optasolver.model.Room;
import ch.fhnw.ip6.optasolver.model.Timeslot;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;

import static ch.fhnw.ip6.common.util.CostUtil.USED_ROOM_COST;

public class MinimizeTimeslotsConstraint extends OptaConstraint {

    @Override
    public Constraint build() {
        return constraintFactory.from(Timeslot.class).penalize("minimize Timeslots", HardSoftScore.ONE_SOFT, (t -> {
            if(t.getPresentationList().size() > 0){
                return t.getPriority();
            } else {
                return 0;
            }
        }));


    }
}
