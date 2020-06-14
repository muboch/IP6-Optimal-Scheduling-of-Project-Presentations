package ch.fhnw.ip6.optasolver.model;

import ch.fhnw.ip6.common.dto.LecturerDto;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

import java.util.List;

@PlanningEntity
public class Lecturer extends LecturerDto {

    @PlanningVariableReference(entityClass = Presentation.class, variableName = "presentations")
    private List<Presentation> presentations;

    @PlanningVariableReference(entityClass = Room.class, variableName = "rooms")
    private List<Room> rooms;

    @PlanningVariableReference(entityClass = Timeslot.class, variableName = "timeslots")
    private List<Timeslot> timeslots;

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

    public void setFreeTimeslots(int freeTimeslots) {
        this.freeTimeslots = freeTimeslots;
    }

    public int getFreeTimeslots() {
        return freeTimeslots;
    }

    public void setRoomSwitches(int roomSwitches) {
        this.roomSwitches = roomSwitches;
    }

    public int getRoomSwitches() {
        return roomSwitches;
    }
}
