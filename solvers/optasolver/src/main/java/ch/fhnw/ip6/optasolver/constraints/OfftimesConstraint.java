package ch.fhnw.ip6.optasolver.constraints;

import ch.fhnw.ip6.optasolver.model.Lecturer;
import ch.fhnw.ip6.optasolver.model.Presentation;
import ch.fhnw.ip6.optasolver.model.Timeslot;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.Joiners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OfftimesConstraint extends OptaConstraint {

    @Override
    public Constraint build() {
        // A lecturer can attend at most one lesson at the same time.
        // get presentation
        return constraintFactory.from(Presentation.class)
                // filter if coach or expert have an offtime for presentation
                .filter(p -> {
                    List<Lecturer> coachAndExpert = new ArrayList<Lecturer>();
                    coachAndExpert.add((Lecturer) p.getCoach());
                    coachAndExpert.add((Lecturer) p.getExpert());
                    Set<Timeslot> coachExpertOfftimes = new HashSet<>();

                    coachAndExpert.forEach(l -> {
                        coachExpertOfftimes.addAll(l.getOfftimes());
                    });
                    return coachExpertOfftimes.contains(p.getTimeslot());
                }).penalize("Offtimes conflict", HardSoftScore.ONE_HARD);
    }
}
