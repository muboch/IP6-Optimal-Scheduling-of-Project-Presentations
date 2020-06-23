package ch.fhnw.ip6.optasolver.constraints;


import ch.fhnw.ip6.optasolver.model.Lecturer;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;

import static ch.fhnw.ip6.common.util.CostUtil.LECTURER_PER_LESSON_COST;


public class MinimizeFreeTimeslotsConstraint extends OptaConstraint {
    @Override
    public Constraint build() {
        // Select a lecturer
        return constraintFactory.from(Lecturer.class)
                .penalize("Free Timeslots",
                        HardSoftScore.ONE_SOFT, (l) -> {
                            if (l.getFreeTimeslots() == null) {
                                System.out.println("NULL");
                                return 999;
                            }
                            return l.getFreeTimeslots() * LECTURER_PER_LESSON_COST;
                        });
    }
}
