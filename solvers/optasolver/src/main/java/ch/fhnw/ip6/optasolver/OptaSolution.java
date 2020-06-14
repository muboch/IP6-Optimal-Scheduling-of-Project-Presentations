package ch.fhnw.ip6.optasolver;


import ch.fhnw.ip6.optasolver.model.Lecturer;
import ch.fhnw.ip6.optasolver.model.Presentation;
import ch.fhnw.ip6.optasolver.model.Room;
import ch.fhnw.ip6.optasolver.model.Timeslot;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import java.util.List;

@PlanningSolution
public class OptaSolution {

    @ProblemFactCollectionProperty
    private List<Lecturer> lecturers;

    @ValueRangeProvider(id = "timeslotRange")
    @ProblemFactCollectionProperty
    private List<Timeslot> timeslots;

    @ValueRangeProvider(id = "roomRange")
    @ProblemFactCollectionProperty
    private List<Room> roomList;

    @PlanningEntityCollectionProperty
    private List<Presentation> presentations;


    @PlanningScore
    private HardSoftScore score;

    private OptaSolution() {
    }


    public OptaSolution(List<Timeslot> timeslots, List<Room> rooms, List<Presentation> presentations, List<Lecturer> lecturers) {
        this.timeslots = timeslots;
        this.roomList = rooms;
        this.presentations = presentations;
        this.lecturers = lecturers;
    }


    // ********************************
    // Getters and setters
    // ********************************


    public List<Lecturer> getLecturers() {
        return lecturers;
    }

    public List<Timeslot> getTimeslots() {
        return timeslots;
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public List<Presentation> getPresentations() {
        return presentations;
    }

    public HardSoftScore getScore() {
        return score;
    }

}