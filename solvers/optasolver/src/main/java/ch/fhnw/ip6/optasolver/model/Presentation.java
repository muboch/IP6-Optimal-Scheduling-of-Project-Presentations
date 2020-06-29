package ch.fhnw.ip6.optasolver.model;

import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.PresentationDto;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class Presentation extends PresentationDto {


    private Lecturer coach;
    private Lecturer expert;

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

    @PlanningId
    @Override
    public int getId() {
        return super.getId();
    }

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

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
