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

                if (getIlpModel().getPresentationsPerLecturer().get(l).size() == 0) {
                    continue;
                }

                for (R r : rooms) {
                    for (T t : timeslots) {

                        GRBLinExpr presAtTimeInRoom = new GRBLinExpr();

                        for (P p1 : getIlpModel().getPresentationsPerLecturer().get(l)) {
                            if (getX()[indexOf(p1)][indexOf(t)][indexOf(r)] != null)
                                presAtTimeInRoom.addTerm(1.0, getX()[indexOf(p1)][indexOf(t)][indexOf(r)]);
                        }
                        getGrbModel().addGenConstrIndicator(lecInRoomAtTime[indexOf(l)][indexOf(t)][indexOf(r)], 1, presAtTimeInRoom, GRB.EQUAL, 1.0, null);

                        GRBLinExpr linExprPrevRoom = new GRBLinExpr();
                        linExprPrevRoom.addConstant(1.0);
                        if (t != firstTimeslot) {
                            linExprPrevRoom.addTerm(-1.0, lecInRoomAtTime[indexOf(l)][indexOf(t) - 1][indexOf(r)]);
                        } else {

                            linExprPrevRoom.addTerm(0.0, lecInRoomAtTime[indexOf(l)][indexOf(t)][indexOf(r)]);
                        }

                        GRBVar currentRoomNotPrevRoom = getGrbModel().addVar(0, 1, 0.0, GRB.BINARY, "currentRoomNotPrevRoom-" + l.getInitials() + "-" + r.getName() + "-" + t.getId());
                        getGrbModel().addGenConstrIndicator(currentRoomNotPrevRoom, 1, linExprPrevRoom, GRB.GREATER_EQUAL, 1.0, null);
                        getObjectives().addTerm(CostUtil.ROOM_SWITCH_COST, currentRoomNotPrevRoom);

                    }

                }

            }

            // subtract cost for each lecturer with min. one presentation. this is because the first usage of a room (entrance) is counted. this is actually false as this is no room switch
            getObjectives().addConstant(-(int) getIlpModel().getPresentationsPerLecturer().entrySet().stream().filter(e -> e.getValue().size() > 0).count() * CostUtil.ROOM_SWITCH_COST);

        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected String getConstraintName() {
        return "MinRoomSwitches";
    }
}
