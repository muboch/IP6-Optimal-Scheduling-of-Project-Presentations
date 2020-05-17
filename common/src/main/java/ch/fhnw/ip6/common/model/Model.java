package ch.fhnw.ip6.common.model;

import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Model<M, X> {

    private final List<? extends P> presentations;
    private final List<? extends L> lecturers;
    private final List<? extends R> rooms;
    private final List<? extends T> timeslots;
    private final Map<L, List<P>> presentationsPerLecturer;
    private final M model;
    private final boolean[][] offtimes;
    private List<L> coaches;
    private List<L> experts;
    private final X[][][] X;

    public Model(List<? extends P> presentations, List<? extends L> lecturers, List<? extends R> rooms, List<? extends T> timeslots, boolean[][] offtimes, M model) {
        this.presentations = presentations;
        this.lecturers = lecturers;
        this.rooms = rooms;
        this.timeslots = timeslots;
        this.offtimes = offtimes;
        this.model = model;
        this.X = setupVars();

        this.presentationsPerLecturer = new HashMap<>();
        for (L l : lecturers) {
            presentationsPerLecturer.put(l, presentations.stream().filter(ps -> ps.getExpert().getId() == l.getId() || ps.getCoach().getId() == l.getId()).collect(Collectors.toList()));
        }

    }

    protected abstract X[][][] setupVars();


    public X[][][] getX() {
        return X;
    }

    public List<P> getPresentations() {
        return Collections.unmodifiableList(presentations);
    }

    public List<T> getTimeslots() {
        return Collections.unmodifiableList(timeslots);
    }

    public List<R> getRooms() {
        return Collections.unmodifiableList(rooms);
    }

    public List<L> getLecturers() {
        return Collections.unmodifiableList(lecturers);
    }

    public boolean[][] getOfftimes() {
        return offtimes;
    }

    public List<L> getCoaches() {
        if (coaches == null) {
            coaches = getPresentations().stream().map(P::getCoach).collect(Collectors.toList());
        }
        return Collections.unmodifiableList(coaches);
    }

    public List<L> getExperts() {
        if (experts == null) {
            experts = getPresentations().stream().map(P::getExpert).collect(Collectors.toList());
        }
        return Collections.unmodifiableList(experts);
    }

    public Map<L, List<P>> getPresentationsPerLecturer() {
        return presentationsPerLecturer;
    }

    public M getModel() {
        return model;
    }

    public int indexOf(T slot) {
        return getTimeslots().indexOf(slot);
    }

    public int indexOf(R room) {
        return getRooms().indexOf(room);
    }

    public int indexOf(P presentation) {
        return getPresentations().indexOf(presentation);
    }

    public int indexOf(L lecturer) {
        return getLecturers().indexOf(lecturer);
    }

}
