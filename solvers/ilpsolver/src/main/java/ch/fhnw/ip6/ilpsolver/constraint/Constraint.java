package ch.fhnw.ip6.ilpsolver.constraint;

import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.common.dto.RoomDto;
import ch.fhnw.ip6.common.dto.TimeslotDto;
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

    protected ILPModel getModel() {
        return model;
    }

    protected GRBModel getGrbModel() {
        return model.getModel();
    }

    protected GRBVar[][][] getX() {
        return getModel().getX();
    }

    public int indexOf(TimeslotDto slot) {
        return getModel().getTimeslots().indexOf(slot);
    }

    public int indexOf(RoomDto room) {
        return getModel().getRooms().indexOf(room);
    }

    public int indexOf(PresentationDto presentation) {
        return getModel().getPresentations().indexOf(presentation);
    }

    public int indexOf(LecturerDto lecturer) {
        return getModel().getLecturers().indexOf(lecturer);
    }

    public Constraint setModel(ILPModel model) {
        this.model = model;
        return this;
    }
}
