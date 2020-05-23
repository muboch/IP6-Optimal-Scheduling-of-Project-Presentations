package ch.fhnw.ip6.originalsolver.callback;

import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.originalsolver.ChocoModel;
import ch.fhnw.ip6.solutionchecker.SolutionChecker;

import java.util.List;

public class ChocoSolverCallback {

    private final Object[][][] x;
    private final List<P> presentations;
    private final List<T> timeslots;
    private final List<R> rooms;
    private final List<L> lecturers;
    private final Object model;
    private final SolutionChecker solutionChecker;

    public ChocoSolverCallback(ChocoModel model) {
        this.x = model.getX();
        this.presentations = model.getPresentations();
        this.timeslots = model.getTimeslots();
        this.rooms = model.getRooms();
        this.lecturers = model.getLecturers();
        this.model = model.getModel();
        this.solutionChecker = new SolutionChecker();
    }

    protected void callback() {


    }
}
