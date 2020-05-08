package ch.fhnw.ip6.ilpsolver.constraint;

import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Timeslot;
import ch.fhnw.ip6.ilpsolver.ILPModel;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public abstract class Constraint {

    private ILPModel model;

    public abstract void build();

    protected abstract String getConstraintName();

    protected void addConstraint(GRBLinExpr lhs, char type) throws GRBException {
        model.getModel().addConstr(lhs, type, 1.0, getConstraintName());
    }

    protected ILPModel getIlpModel() {
        return model;
    }

    protected GRBModel getGrbModel() {
        return model.getModel();
    }

    protected GRBVar[][][] getX() {
        return model.getX();
    }

    public int indexOf(Timeslot slot) {
        return model.getTimeslots().indexOf(slot);
    }

    public int indexOf(Room room) {
        return model.getRooms().indexOf(room);
    }

    public int indexOf(Presentation presentation) {
        return model.getPresentations().indexOf(presentation);
    }

    public int indexOf(Lecturer lecturer) {
        return model.getLecturers().indexOf(lecturer);
    }

    public Constraint setModel(ILPModel model) {
        this.model = model;
        return this;
    }
}
