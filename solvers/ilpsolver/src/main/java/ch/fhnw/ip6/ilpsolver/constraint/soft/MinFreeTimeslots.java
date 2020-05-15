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

import static ch.fhnw.ip6.common.util.CostUtil.LECTURER_PER_LESSON_COST;

/**
 * 1. Coaches sollen den Room möglichst selten wechseln.
 */
public class MinFreeTimeslots extends SoftConstraint {
    @Override
    public void build() {
        try {

            GRBVar[][] lecturerTimeslot = new GRBVar[getIlpModel().getLecturers().size()][getIlpModel().getTimeslots().size()]; // Coach has a presentation at timeslot

            GRBVar[] firstTimeslots = new GRBVar[getIlpModel().getLecturers().size()];
            GRBVar[] diffs = new GRBVar[getIlpModel().getLecturers().size()];
            GRBVar[] lastTimeslots = new GRBVar[getIlpModel().getLecturers().size()];

            for (L l : getIlpModel().getLecturers()) {
                for (T t : getIlpModel().getTimeslots()) {
                    lecturerTimeslot[indexOf(l)][indexOf(t)] = getGrbModel().addVar(0, 1, 1.0, GRB.BINARY, l.getInitials() + "." + t.getDate());

                    GRBLinExpr lhs = new GRBLinExpr();
                    for (R r : getIlpModel().getRooms()) {
                        for (P p : getIlpModel().getPresentations()) {
                            lhs.addTerm(1.0, getX()[indexOf(p)][indexOf(t)][indexOf(r)]);
                        }
                    }

                    getGrbModel().addGenConstrIndicator(lecturerTimeslot[indexOf(l)][indexOf(t)], 0, lhs, GRB.LESS_EQUAL, 0.0, "notUsed" + t.getDate());
                    getGrbModel().addGenConstrIndicator(lecturerTimeslot[indexOf(l)][indexOf(t)], 1, lhs, GRB.GREATER_EQUAL, 1.0, "used" + t.getDate());
                }
            }
            for (L l : getIlpModel().getLecturers()) {

                firstTimeslots[indexOf(l)] = getGrbModel().addVar(0.0, getIlpModel().getTimeslots().size(), 1.0, GRB.INTEGER, "first" + l.getInitials());
                lastTimeslots[indexOf(l)] = getGrbModel().addVar(0.0, getIlpModel().getTimeslots().size(), 1.0, GRB.INTEGER, "last" + l.getInitials());
                diffs[indexOf(l)] = getGrbModel().addVar(0.0, getIlpModel().getTimeslots().size(), 1.0, GRB.INTEGER, "diff" + l.getInitials());

                for (T t : getIlpModel().getTimeslots()) {
                    GRBLinExpr lhsLast = new GRBLinExpr();
                    lhsLast.addTerm(1.0, lastTimeslots[indexOf(l)]);
                    getGrbModel().addGenConstrIndicator(lecturerTimeslot[indexOf(l)][indexOf(t)], 0, lhsLast, GRB.LESS_EQUAL, 0.0, "last" + t.getDate());

                    GRBLinExpr lhsFirst = new GRBLinExpr();
                    lhsFirst.addTerm(1.0, firstTimeslots[indexOf(l)]);
                    getGrbModel().addGenConstrIndicator(lecturerTimeslot[indexOf(l)][indexOf(t)], 1, lhsFirst, GRB.GREATER_EQUAL, 1.0, "first" + t.getDate());
                }
                getObjectives().addTerm(LECTURER_PER_LESSON_COST, diffs[indexOf(l)]);
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
