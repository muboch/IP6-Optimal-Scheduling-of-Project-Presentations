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

import java.util.Comparator;
import java.util.List;

/**
 * 1. Coaches sollen den Room möglichst selten wechseln.
 */
public class MinRoomSwitches extends SoftConstraint {
    @Override
    public void build() {

        try {
            // Variable / Array setup for all the things
            List<L> lecturers = getIlpModel().getLecturers();
            List<T> timeslots = getIlpModel().getTimeslots();
            List<R> rooms = getIlpModel().getRooms();

            T firstTimeslot = timeslots.stream().min(Comparator.comparingInt(T::getId)).get();
            GRBVar[][][] lecInRoomAtTime = new GRBVar[lecturers.size()][timeslots.size()][rooms.size()];

            for (L l : lecturers) {
                for (T t : timeslots) {
                    for (R r : rooms) {
                        lecInRoomAtTime[indexOf(l)][indexOf(t)][indexOf(r)] = getGrbModel().addVar(0, 1, 1.0, GRB.BINARY, null);
                    }
                }
            }

            for (L l : lecturers) {
                for (R r : rooms) {
                    for (T t : timeslots) {

                        GRBLinExpr presAtTimeInRoom = new GRBLinExpr();

                        for (P p1 : getIlpModel().getPresentationsPerLecturer().get(l)) {
                            if (getX()[indexOf(p1)][indexOf(t)][indexOf(r)] != null)
                                presAtTimeInRoom.addTerm(1.0, getX()[indexOf(p1)][indexOf(t)][indexOf(r)]);
                        }
                        getGrbModel().addConstr(lecInRoomAtTime[indexOf(l)][indexOf(t)][indexOf(r)], GRB.EQUAL, presAtTimeInRoom, null);

                        GRBLinExpr linExprPrevRoom = new GRBLinExpr();
                        linExprPrevRoom.addConstant(1.0);
                        if (t != firstTimeslot) {
                            linExprPrevRoom.addTerm(-1.0, lecInRoomAtTime[indexOf(l)][indexOf(t) - 1][indexOf(r)]);
                        } else {
                            linExprPrevRoom.addTerm(0.0, lecInRoomAtTime[indexOf(l)][indexOf(t)][indexOf(r)]);
                        }

                        GRBVar currentRoomNotPrevRoom = getGrbModel().addVar(0, 1, 0.0, GRB.BINARY, "currentRoomNotPrevRoom-" + l.getInitials() + "-" + r.getName() + "-" + t.getId());

                        //                {0,1}                       {0,1}
                        // ---> current == prev
                        // rhs = 1.0 * prt[l][t][r] + (1 - 1.0 * prt[l][t-1][r]) - 1
                        // rhs = 1.0 *       1      + (1 - 1.0 *  1            ) - 1
                        // rhs = 1 + (0) - 1
                        // rhs = 0
                        // ---- current != prev
                        // rhs = 1.0 *       1      + (1 - 1.0 * 0             ) - 1
                        // rhs = 1 + (1 - 0) - 1
                        // rhs = 1
                        // ---- current != prev
                        // rhs = 1.0 *       0      + (1 - 1.0 * 1             ) - 1
                        // rhs = 0 + (1 - 1) - 1
                        // rhs = -1
                        GRBLinExpr rhs = new GRBLinExpr();
                        rhs.addTerm(1.0, lecInRoomAtTime[indexOf(l)][indexOf(t)][indexOf(r)]);
                        rhs.multAdd(1.0, linExprPrevRoom);
                        rhs.addConstant(-1);
                        // {0,1} >= rhs
                        getGrbModel().addConstr(currentRoomNotPrevRoom, GRB.GREATER_EQUAL, rhs, "ConstrCurrentRoomNotPrevRoom-" + l.getInitials() + "-" + r.getName() + "-" + t.getId());
                        // FIXME: Bug cost are wrong
                        getObjectives().addTerm(CostUtil.ROOM_SWITCH_COST, currentRoomNotPrevRoom);
                    }
                }
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
