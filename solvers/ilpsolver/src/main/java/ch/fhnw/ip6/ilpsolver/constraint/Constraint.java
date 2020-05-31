package ch.fhnw.ip6.ilpsolver.constraint;

import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.ilpsolver.ILPModel;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

public abstract class Constraint {

    private ILPModel model;
    private GRBLinExpr objectives;

    public abstract void build();

    protected abstract String getConstraintName();

    protected void addConstraint(GRBLinExpr lhs, char type) throws GRBException {
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

    public int indexOf(T slot) {
        return model.getTimeslots().indexOf(slot);
    }

    public int indexOf(R room) {
        return model.getRooms().indexOf(room);
    }

    public int indexOf(P presentation) {
        return model.getPresentations().indexOf(presentation);
    }

    public int indexOf(L lecturer) {
        return model.getLecturers().indexOf(lecturer);
    }

    public Constraint setModel(ILPModel model) {
        this.model = model;
        return this;
    }

    public void setObjectives(GRBLinExpr objectives) {
        this.objectives = objectives;
    }

    public GRBLinExpr getObjectives() {
        return objectives;
    }
}
