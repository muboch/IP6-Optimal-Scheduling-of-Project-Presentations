package ch.fhnw.ip6.ilpsolver.constraint;

import gurobi.GRBLinExpr;

public abstract class SoftConstraint extends Constraint {

    private GRBLinExpr objectives;

    public void setObjectives(GRBLinExpr objectives) {
        this.objectives = objectives;
    }

    public GRBLinExpr getObjectives() {
        return objectives;
    }


}
