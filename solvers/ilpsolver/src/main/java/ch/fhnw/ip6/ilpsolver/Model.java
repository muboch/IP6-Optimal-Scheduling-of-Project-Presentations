package ch.fhnw.ip6.ilpsolver;

import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Timeslot;
import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Model {

    private final List<Presentation> presentations;
    private final List<Lecturer> lecturers;
    private final List<Room> rooms;
    private final List<Timeslot> timeslots;
    private final Map<Lecturer, List<Presentation>> presentationsPerLecturer;
    private final GRBModel grbModel;
    private List<Lecturer> coaches;
    private List<Lecturer> experts;
    private final GRBVar[][][] X;

    public Model(List<Presentation> presentations, List<Lecturer> lecturers, List<Room> rooms, List<Timeslot> timeslots, boolean[][] locktimes, GRBModel grbModel) throws GRBException {
        this.presentations = presentations;
        this.lecturers = lecturers;
        this.rooms = rooms;
        this.timeslots = timeslots;
        this.grbModel = grbModel;
        this.X = setupGrbVars();


        this.presentationsPerLecturer = new HashMap<>();
        for (Lecturer l : lecturers) {
            presentationsPerLecturer.put(l, presentations.stream().filter(ps -> ps.getExpert().getId() == l.getId() || ps.getCoach().getId() == l.getId()).collect(Collectors.toList()));
        }

    }

    private GRBVar[][][] setupGrbVars() throws GRBException {
        GRBVar[][][] X = new GRBVar[presentations.size()][timeslots.size()][rooms.size()];
        for (int p = 0; p < presentations.size(); ++p) {
            for (int t = 0; t < timeslots.size(); ++t) {
                for (int r = 0; r < rooms.size(); ++r) {
                    X[p][t][r] = grbModel.addVar(0, 1, 1.0, GRB.BINARY, presentations.get(p) + "." + timeslots.get(t) + "." + rooms.get(r));
                }
            }
        }
        return X;
    }

    public GRBVar[][][] getX() {
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

    public GRBModel getGrbModel() {
        return grbModel;
    }
}
