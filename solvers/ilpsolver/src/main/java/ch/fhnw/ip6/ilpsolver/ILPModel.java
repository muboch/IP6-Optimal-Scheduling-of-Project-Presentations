package ch.fhnw.ip6.ilpsolver;

import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Timeslot;
import ch.fhnw.ip6.common.model.Model;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.List;

public class ILPModel extends Model<GRBModel, GRBVar> {

    public ILPModel(List<Presentation> presentations, List<Lecturer> lecturers, List<Room> rooms, List<Timeslot> timeslots, boolean[][] locktimes, GRBModel model) {
        super(presentations, lecturers, rooms, timeslots, locktimes, model);
    }

    @Override
    protected GRBVar[][][] setupVars() {
        return new GRBVar[0][][];
    }
}
