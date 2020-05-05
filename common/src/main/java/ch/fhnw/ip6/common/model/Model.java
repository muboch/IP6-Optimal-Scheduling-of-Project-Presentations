package ch.fhnw.ip6.common.model;

import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Timeslot;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Model<M, X> {

    private final List<Presentation> presentations;
    private final List<Lecturer> lecturers;
    private final List<Room> rooms;
    private final List<Timeslot> timeslots;
    private final Map<Lecturer, List<Presentation>> presentationsPerLecturer;
    private final M model;
    private final boolean[][] offtimes;
    private List<Lecturer> coaches;
    private List<Lecturer> experts;
    private final X[][][] X;

    public Model(List<Presentation> presentations, List<Lecturer> lecturers, List<Room> rooms, List<Timeslot> timeslots, boolean[][] offtimes, M model) {
        this.presentations = presentations;
        this.lecturers = lecturers;
        this.rooms = rooms;
        this.timeslots = timeslots;
        this.offtimes = offtimes;
        this.model = model;
        this.X = setupVars();

        this.presentationsPerLecturer = new HashMap<>();
        for (Lecturer l : lecturers) {
            presentationsPerLecturer.put(l, presentations.stream().filter(ps -> ps.getExpert().getId() == l.getId() || ps.getCoach().getId() == l.getId()).collect(Collectors.toList()));
        }

    }

    protected abstract X[][][] setupVars();


    public X[][][] getX() {
        return X;
    }

    public List<Presentation> getPresentations() {
        return Collections.unmodifiableList(presentations);
    }

    public List<Timeslot> getTimeslots() {
        return Collections.unmodifiableList(timeslots);
    }

    public List<Room> getRooms() {
        return Collections.unmodifiableList(rooms);
    }

    public List<Lecturer> getLecturers() {
        return Collections.unmodifiableList(lecturers);
    }

    public boolean[][] getOfftimes() {
        return offtimes;
    }

    public List<Lecturer> getCoaches() {
        if (coaches == null) {
            coaches = getPresentations().stream().map(Presentation::getCoach).collect(Collectors.toList());
        }
        return Collections.unmodifiableList(coaches);
    }

    public List<Lecturer> getExperts() {
        if (experts == null) {
            experts = getPresentations().stream().map(Presentation::getExpert).collect(Collectors.toList());
        }
        return Collections.unmodifiableList(experts);
    }

    public Map<Lecturer, List<Presentation>> getPresentationsPerLecturer() {
        return presentationsPerLecturer;
    }

    public M getModel() {
        return model;
    }

    public int indexOf(Timeslot slot) {
        return getTimeslots().indexOf(slot);
    }

    public int indexOf(Room room) {
        return getRooms().indexOf(room);
    }

    public int indexOf(Presentation presentation) {
        return getPresentations().indexOf(presentation);
    }

    public int indexOf(Lecturer lecturer) {
        return getLecturers().indexOf(lecturer);
    }

}
