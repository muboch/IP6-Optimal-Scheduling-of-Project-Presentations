package ch.fhnw.ip6.ilpsolver.constraint;

import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Timeslot;
import ch.fhnw.ip6.ilpsolver.Model;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public abstract class Constraint {

    private Model model;

    public abstract void build();

    protected abstract String getConstraintName();

    protected void addConstraint(GRBLinExpr lhs, char type) throws GRBException {
        getModel().getGrbModel().addConstr(lhs, type, 1.0, getConstraintName());
    }

    protected Model getModel() {
        return model;
    }

    protected GRBModel getGrbModel() {
        return getModel().getGrbModel();
    }

    protected GRBVar[][][] getX() {
        return getModel().getX();
    }

    public int indexOf(Timeslot slot) {
        return getModel().getTimeslots().indexOf(slot);
    }

    public int indexOf(Room room) {
        return getModel().getRooms().indexOf(room);
    }

    public int indexOf(Presentation presentation) {
        return getModel().getPresentations().indexOf(presentation);
    }

    public int indexOf(Lecturer lecturer) {
        return getModel().getLecturers().indexOf(lecturer);
    }

    public Constraint setModel(Model model) {
        this.model = model;
        return this;
    }
}
