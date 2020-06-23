package ch.fhnw.ip6.ilpsolver.constraint.soft;

import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.common.util.CostUtil;
import ch.fhnw.ip6.ilpsolver.constraint.SoftConstraint;
import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBVar;

/**
 * 1. Coaches sollen den Room möglichst selten wechseln.
 */
public class MinFreeTimeslots extends SoftConstraint {
    @Override
    public void build() {
        try {

            int UB = getIlpModel().getTimeslots().size();

            GRBVar[][] lecTime = new GRBVar[getIlpModel().getLecturers().size()][getIlpModel().getTimeslots().size()];

            for (L l : getIlpModel().getLecturers()) {
                for (T t : getIlpModel().getTimeslots()) {
                    lecTime[indexOf(l)][indexOf(t)] = getGrbModel().addVar(0, 1, 0, GRB.BINARY, null);
                    GRBLinExpr sum = new GRBLinExpr();
                    for (P p : getIlpModel().getPresentationsPerLecturer().get(l)) {
                        for (R r : getIlpModel().getRooms()) {
                            if (getX()[indexOf(p)][indexOf(t)][indexOf(r)] != null) {
                                sum.addTerm(1.0, getX()[indexOf(p)][indexOf(t)][indexOf(r)]);
                            }
                        }
                    }
                    getGrbModel().addConstr(lecTime[indexOf(l)][indexOf(t)], GRB.EQUAL, sum, null);
                }
            }

            for (L l : getIlpModel().getLecturers()) {

                GRBVar diff = getGrbModel().addVar(0, UB, 0, GRB.INTEGER, "diff[" + l.getId() + "]");

                for (T tBeg : getIlpModel().getTimeslots()) {
                    for (T tEnd : getIlpModel().getTimeslots()) {

                        // We only use the upper triangular matrix including the diagonal
                        if (tBeg.getId() > tEnd.getId())
                            continue;

                        // used = tBeg & tEnd
                        GRBVar used = getGrbModel().addVar(0, 1, 0, GRB.BINARY, "t[" + l.getId() + "-used-" + tBeg.getId() + "-" + tEnd.getId());
                        GRBLinExpr usedExpr = new GRBLinExpr();
                        usedExpr.addTerm(1.0, lecTime[indexOf(l)][indexOf(tBeg)]);
                        usedExpr.addTerm(1.0, lecTime[indexOf(l)][indexOf(tEnd)]);
                        usedExpr.addConstant(-1);
                        getGrbModel().addConstr(used, GRB.GREATER_EQUAL, usedExpr, null);

                        int abs = tEnd.getId() - tBeg.getId() - getIlpModel().getPresentationsPerLecturer().get(l).size();

                        GRBLinExpr rhs = new GRBLinExpr();
                        rhs.addTerm(abs, used);

                        getGrbModel().addConstr(diff, GRB.GREATER_EQUAL, rhs, null);

                    }
                }

                getObjectives().addTerm(CostUtil.LECTURER_PER_LESSON_COST, diff);

            }

        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getConstraintName() {
        return "MinFreeTimeslots";
    }
}
