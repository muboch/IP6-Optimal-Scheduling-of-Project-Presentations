package ch.fhnw.ip6.ilpsolver.constraint.soft;

import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.ilpsolver.constraint.SoftConstraint;
import gurobi.GRB;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBVar;

import java.util.Comparator;

import static ch.fhnw.ip6.common.util.CostUtil.LECTURER_PER_LESSON_COST;

/**
 * 1. Coaches sollen den Room möglichst selten wechseln.
 */
public class MinFreeTimeslots extends SoftConstraint {
    @Override
    public void build() {
        try {

            T t0 = getIlpModel().getTimeslots().stream().min(Comparator.comparingInt(T::getId)).get();

            for (L l : getIlpModel().getLecturers()) {
                GRBVar min = getGrbModel().addVar(0.0, getIlpModel().getTimeslots().size(), 0.0, GRB.INTEGER, "min" + l);
                GRBVar max = getGrbModel().addVar(0.0, getIlpModel().getTimeslots().size(), 0.0, GRB.INTEGER, "max" + l);
                for (T t : getIlpModel().getTimeslots()) {
                    GRBLinExpr rhs = new GRBLinExpr();
                    for (R r : getIlpModel().getRooms()) {
                        for (P p : getIlpModel().getPresentationsPerLecturer().get(l)) {
                            rhs.addTerm(1.0, getX()[indexOf(p)][indexOf(t)][indexOf(r)]);
                            if (t != t0) {
                                GRBLinExpr prevLinExpr = new GRBLinExpr();
                                prevLinExpr.addConstant(1);
                                prevLinExpr.addTerm(-1.0, getX()[indexOf(p)][indexOf(t) - 1][indexOf(r)]);
                                rhs.add(prevLinExpr);
                            }
                            rhs.addTerm(-1.0, min);
                        }
                    }
                    getGrbModel().addConstr(min, GRB.GREATER_EQUAL, rhs, null);
                    getGrbModel().addConstr(max, GRB.LESS_EQUAL, getIlpModel().getTimeslots().size(), null);
                    // firstTimeslot >= 1 * prt[p][t][r] + (1 - prt[p][t-1][r]) - firstTimeslot
                    // lastTimeslot <= timeslots.size()

                }

                GRBLinExpr abs = new GRBLinExpr();
                abs.addTerm(1.0, max);
                abs.addTerm(-1.0, min);

                getObjectives().multAdd(LECTURER_PER_LESSON_COST, abs);

            }

        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getConstraintName() {
        return "MinRoomSwitches";
    }
}
