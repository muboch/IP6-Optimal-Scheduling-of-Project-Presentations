package ch.fhnw.ip6.optasolver.constraints;


import ch.fhnw.ip6.common.dto.LecturerDto;
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
                                return 999;
                            }
                            return l.getFreeTimeslots() * LECTURER_PER_LESSON_COST;
                        });


        /*
        // Select a lecturer
        return constraintFactory.from(LecturerDto.class)
                .groupBy(LecturerDto::getPresentations, toList())
                .penalize("Shift on an off-day",
                        HardSoftScore.ONE_SOFT, (ps,l) -> {

                            try {
                                PresentationDto firstPres = ps.stream().min(Comparator.comparingInt(p -> p.getTimeslot().getId())).get(); // change timeslot.getId to timeslot.getOrder
                                PresentationDto lastPres = ps.stream().max(Comparator.comparingInt(p -> p.getTimeslot().getId())).get(); // change timeslot.getId to timeslot.getOrder
                                int numPres = ps.size();
                                // Number of Free timeslots is = numPres - (lastPres - firstPres);
                                return (numPres - (lastPres.getTimeslot().getId() - firstPres.getTimeslot().getId())) * LECTURER_PER_LESSON_COST;
                            } catch (Exception e) {
                                return 999;
                            }

                        });

        /*

        return constraintFactory.from(LecturerDto.class)
                .penalize("Shift on an off-day",
                        HardSoftScore.ONE_SOFT, (l) -> {

                            try {
                                PresentationDto firstPres = l.getPresentations().stream().min(Comparator.comparingInt(p -> p.getTimeslot().getId())).get(); // change timeslot.getId to timeslot.getOrder
                                PresentationDto lastPres = l.getPresentations().stream().max(Comparator.comparingInt(p -> p.getTimeslot().getId())).get(); // change timeslot.getId to timeslot.getOrder
                                int numPres = l.getPresentations().size();
                                // Number of Free timeslots is = numPres - (lastPres - firstPres);
                                return (numPres - (lastPres.getTimeslot().getId() - firstPres.getTimeslot().getId())) * LECTURER_PER_LESSON_COST;
                            } catch (Exception e) {
                                return 999;
                            }

                        });

    }

         */

    }
}
