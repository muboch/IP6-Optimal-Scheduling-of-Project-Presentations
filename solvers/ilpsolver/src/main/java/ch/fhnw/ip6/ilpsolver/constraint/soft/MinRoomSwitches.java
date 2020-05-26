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
            List<P> presentations = getIlpModel().getPresentations();

            T firstTimeslot = timeslots.stream().min(Comparator.comparingInt(T::getId)).get();
            System.out.println(firstTimeslot);
            GRBLinExpr sumAllSwitches = new GRBLinExpr();
            for (L l : lecturers) {

                for (R r : rooms) {
                    for (T t : timeslots) {

                        GRBLinExpr linExprPrevRoom = new GRBLinExpr();
                        linExprPrevRoom.addConstant(1.0);
                        if (t != firstTimeslot) {
                            linExprPrevRoom.addTerm(-1.0, getX()[indexOf(l)][indexOf(t) - 1][indexOf(r)]);
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
                        GRBLinExpr rhs = new GRBLinExpr();
                        rhs.addTerm(1.0, getX()[indexOf(l)][indexOf(t)][indexOf(r)]);
                        rhs.multAdd(1.0, linExprPrevRoom);
                        rhs.addConstant(-1);
                        getGrbModel().addConstr(currentRoomNotPrevRoom, GRB.GREATER_EQUAL, rhs, "ConstrCurrentRoomNotPrevRoom-" + l.getInitials() + "-" + r.getName() + "-" + t.getId());

                        sumAllSwitches.add(rhs);
                    }
                }
            }
            getObjectives().multAdd(CostUtil.ROOM_SWITCH_COST, sumAllSwitches);


        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getConstraintName() {
        return "MinRoomSwitches";
    }
}
