package ch.fhnw.ip6.optasolver.model;

import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.optasolver.constraints.varlistener.FreeTimeslotsUpdatingVarListener;
import ch.fhnw.ip6.optasolver.constraints.varlistener.RoomSwitchesUpdatingVarListener;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

import java.util.ArrayList;
import java.util.List;

@PlanningEntity
public class Lecturer extends LecturerDto {

    @PlanningVariableReference(entityClass = Presentation.class, variableName = "presentations")
    private List<Presentation> presentations;

    @PlanningVariableReference(entityClass = Room.class, variableName = "rooms")
    private List<Room> rooms;

    @PlanningVariableReference(entityClass = Timeslot.class, variableName = "timeslots")
    private List<Timeslot> timeslots;
    private List<Timeslot> offtimes;

    private int freeTimeslots;
    private int roomSwitches;

    public List<Presentation> getPresentations() {
        return presentations;
    }

    public void setPresentations(List<Presentation> presentations) {
        this.presentations = presentations;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    public List<Timeslot> getTimeslots() {
        return timeslots;
    }

    public void setTimeslots(List<Timeslot> timeslots) {
        this.timeslots = timeslots;
    }

    public List<Timeslot> getOfftimes() {
        if(offtimes == null){offtimes = new ArrayList<>();
        }

        return offtimes;
    }

    public void setOfftimes(List<Timeslot> offtimes) {
        this.offtimes = offtimes;
    }


    public void setFreeTimeslots(int freeTimeslots) {
        this.freeTimeslots = freeTimeslots;
    }

    @CustomShadowVariable(variableListenerClass = FreeTimeslotsUpdatingVarListener.class,
            sources = {@PlanningVariableReference(variableName = "timeslot", entityClass = Presentation.class)})
    public Integer getFreeTimeslots() {
        return freeTimeslots;
    }


    public void setRoomSwitches(int roomSwitches) {
        this.roomSwitches = roomSwitches;
    }

    @CustomShadowVariable(variableListenerClass = RoomSwitchesUpdatingVarListener.class,
            sources = {@PlanningVariableReference(variableName = "room", entityClass = Presentation.class)})
    public Integer getRoomSwitches() {
        return roomSwitches;
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
