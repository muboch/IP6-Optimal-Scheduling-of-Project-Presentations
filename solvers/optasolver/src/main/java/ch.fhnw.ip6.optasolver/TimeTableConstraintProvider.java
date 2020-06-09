package ch.fhnw.ip6.optasolver;

import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.ospp.model.Lecturer;
import org.apache.tomcat.util.bcel.Const;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

public class TimeTableConstraintProvider implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[]{
                // Hard constraints
                roomConflict(constraintFactory),
                roomTypeConflict(constraintFactory),
                coachAndExpertSameConflict(constraintFactory),
                // offtimeconflict

                // Soft constraints
                minimizeRooms(constraintFactory),
                minimizeTimeslots(constraintFactory)


        };
    }

    private Constraint roomTypeConflict ( ConstraintFactory constraintFactory){
        return constraintFactory.from(PresentationDto.class).filter((pres) -> {
            return !pres.getType().equals(pres.getRoom().getType());
        }).penalize("RoomType conflict", HardSoftScore.ONE_HARD);
    }
    private Constraint minimizeRooms (ConstraintFactory constraintFactory){
        return constraintFactory.from(PresentationDto.class).groupBy(p -> p.getRoom()).penalize("minimize Rooms", HardSoftScore.ONE_SOFT);
    }
    private Constraint minimizeTimeslots (ConstraintFactory constraintFactory){
        return constraintFactory.from(PresentationDto.class).groupBy(p -> p.getTimeslot()).penalize("minimize Timeslots", HardSoftScore.ONE_SOFT);
    }



    private Constraint roomConflict(ConstraintFactory constraintFactory) {
        // A room can accommodate at most one presentation at the same time.

        // Select a lesson ...
        return constraintFactory.from(PresentationDto.class)
                // ... and pair it with another presentation ...
                .join(PresentationDto.class,
                        // ... in the same timeslot ...
                        Joiners.equal(PresentationDto::getTimeslot),
                        // ... in the same room ...
                        Joiners.equal(PresentationDto::getRoom),
                        // ... and the pair is unique (different id, no reverse pairs)
                        Joiners.lessThan(PresentationDto::getId))
                // then penalize each pair with a hard weight.
                .penalize("Room conflict", HardSoftScore.ONE_HARD);
    }


    private Constraint coachAndExpertSameConflict(ConstraintFactory constraintFactory) {
        // A lecturer can attend at most one lesson at the same time.
        // get presentation
        return constraintFactory.from(PresentationDto.class)
                // pair with another presentation
                .join(PresentationDto.class,
                        // in the same timeslot
                        Joiners.equal(PresentationDto::getTimeslot))
                // filter if coach and expert are the same somewhere
                .filter((p1, p2) -> {
                    return p1.getExpert() == p2.getExpert() || p1.getCoach() == p2.getCoach() || p1.getExpert() == p2.getCoach() || p1.getCoach() == p2.getExpert();
                }).penalize("Expert/Coach conflict", HardSoftScore.ONE_HARD); // penalize with 1 hard constraint
    }
}