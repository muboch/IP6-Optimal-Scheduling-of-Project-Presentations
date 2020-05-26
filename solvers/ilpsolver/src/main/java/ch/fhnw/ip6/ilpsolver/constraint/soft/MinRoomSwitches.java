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

            GRBVar[][][] lecturerHasPresAtTime = new GRBVar[lecturers.size()][timeslots.size()][presentations.size()];

            GRBVar[][] roomSwitches = new GRBVar[lecturers.size()][timeslots.size()];
            GRBVar[] numSwitchesPerLecturer = new GRBVar[lecturers.size()];

            for (L l : lecturers) {
                for (T t : timeslots) {
                    for (P p : presentations) {
                        lecturerHasPresAtTime[indexOf(l)][indexOf(t)][indexOf(p)] = getGrbModel().addVar(0, 1, 1.0, GRB.BINARY, "[B=LecHasPresAtTime][" + "l" + l.getId() + ",t" + t.getId() + ",p" + p.getId() + "]");
                    }
                    roomSwitches[indexOf(l)][indexOf(t)] = getGrbModel().addVar(0, 1, 1.0, GRB.BINARY, "[B=LecRoomSwitch][" + "l" + l.getId() + ",t" + t.getId() + "]");
                }
                // min 0, max number of timeslots
                numSwitchesPerLecturer[indexOf(l)] = getGrbModel().addVar(0.0, timeslots.size(), 1.0, GRB.INTEGER, "[I=LecNumRomSwitches][l" + l.getId() + "]");
            }


            // if [l][t][p] == 1
            //    if t = 0
            //        continue
            //    if [l][t][r] != [l][t-1][r]
            //        roomSwitches[l] = +1
            T firstTimeslot = timeslots.stream().min(Comparator.comparingInt(T::getId)).get();

            GRBLinExpr sumAllSwitches = new GRBLinExpr();
            for (L l : lecturers) {
                for (R r : rooms) {
                    for (T t : timeslots) {

                        GRBLinExpr lhs = new GRBLinExpr();
                        for (P p1 : getIlpModel().getPresentationsPerLecturer().get(l)) {
                            lhs.addTerm(1.0, getX()[indexOf(p1)][indexOf(t)][indexOf(r)]);
                        }
                        // If a presentation is happening in room at time, true, else false.
                        getGrbModel().addGenConstrIndicator(lecturerHasPresAtTime[indexOf(l)][indexOf(t)][indexOf(r)], 1, lhs, GRB.EQUAL, 1.0, "[C=PresIsInRoomAtTime][l" + l.getId() + ",t" + t.getId() + ",r" + r.getId() + "]");
                        getGrbModel().addGenConstrIndicator(lecturerHasPresAtTime[indexOf(l)][indexOf(t)][indexOf(r)], 0, lhs, GRB.EQUAL, 0.0, "[C=PresIsNotInRoomAtTime][l" + l.getId() + ",t" + t.getId() + ",r" + r.getId() + "]");

                        GRBLinExpr linExprSecodRoom = new GRBLinExpr();
                        linExprSecodRoom.addTerm(1.0, lecturerHasPresAtTime[indexOf(l)][indexOf(t)][indexOf(r)]);

                        GRBLinExpr linExprPrevRoom = new GRBLinExpr();
                        linExprPrevRoom.addConstant(1);
                        if (t == firstTimeslot) {
                        } else {
                            linExprPrevRoom.addTerm(-1.0, lecturerHasPresAtTime[indexOf(l)][indexOf(t) - 1][indexOf(r)]);
                        }
                        GRBVar secondRoomNotPrevRoom = getGrbModel().addVar(0, 1, 0, GRB.BINARY, "B-secondRoomNotPrevRoom");

                        // getGrbModel().addConstr(secondRoomNotPrevRoom, GRB.LESS_EQUAL, lecturerHasPresAtTime[indexOf(l)][indexOf(t)][indexOf(r)], null);  //    x <= a
                        // getGrbModel().addConstr(secondRoomNotPrevRoom, GRB.LESS_EQUAL, linExprPrevRoom, null);//    x <= b
                        GRBLinExpr rhs = new GRBLinExpr();
                        rhs.addTerm(1.0, lecturerHasPresAtTime[indexOf(l)][indexOf(t)][indexOf(r)]);
                        rhs.multAdd(1.0, linExprSecodRoom);
                        rhs.addConstant(-1);
                        getGrbModel().addConstr(secondRoomNotPrevRoom, GRB.GREATER_EQUAL, rhs, null);
                        sumAllSwitches.addTerm(1.0, secondRoomNotPrevRoom);
                        //[13:56] simon.felix@ateleris.ch
                        //    x = a & b
                        //​[13:56] simon.felix@ateleris.ch
                        //    x <= a
                        //​[13:56] simon.felix@ateleris.ch
                        //    x <= b
                        //​[13:57] simon.felix@ateleris.ch
                        //    x >= a + b - 1
                        //​[13:57] simon.felix@ateleris.ch
                        //    x = a & !b
                        //​[13:57] simon.felix@ateleris.ch
                        //    x = a & (1-b)

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
