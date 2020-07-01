package ch.fhnw.ip6.optasolver.model;

import ch.fhnw.ip6.common.dto.PresentationDto;
import lombok.EqualsAndHashCode;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
@EqualsAndHashCode(callSuper = true)
public class Presentation extends PresentationDto {


    private Lecturer coach;
    private Lecturer expert;

    @PlanningId
    public Long getPlanningId(){
        return (long) super.getId();
    }

    @Override
    public Lecturer getExpert() {
        return expert;
    }

    public void setExpert(Lecturer expert) {
        this.expert = expert;
    }

    public void setCoach(Lecturer coach) {
        this.coach = coach;
    }

    public Lecturer getCoach() {
        return coach;
    }

    @PlanningVariable(valueRangeProviderRefs = "timeslotRange")
    private Timeslot timeslot;

    @PlanningVariable(valueRangeProviderRefs = "roomRange")
    private Room room;

    public Timeslot getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

}
