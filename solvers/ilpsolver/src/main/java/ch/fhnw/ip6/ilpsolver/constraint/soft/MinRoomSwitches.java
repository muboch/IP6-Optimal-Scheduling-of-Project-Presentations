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

            for (L l : lecturers) {
                GRBLinExpr sumAllSwitches = new GRBLinExpr();
                for (T t : timeslots) {
                    for (R r : rooms) {
                        GRBLinExpr lhs = new GRBLinExpr();
                        if(getIlpModel().getPresentationsPerLecturer().get(l).size() < 2) continue;
                        for (P p1 : getIlpModel().getPresentationsPerLecturer().get(l)) {
                            lhs.addTerm(1.0, getX()[indexOf(p1)][indexOf(t)][indexOf(r)]);
                        }
                        // If a presentation is happening in room at time, true, else false.
                        getGrbModel().addGenConstrIndicator(lecturerHasPresAtTime[indexOf(l)][indexOf(t)][indexOf(r)], 1, lhs, GRB.EQUAL, 1.0, "[C=PresIsInRoomAtTime][l" + l.getId() + ",t" + t.getId() + ",r" + r.getId() + "]");
                        getGrbModel().addGenConstrIndicator(lecturerHasPresAtTime[indexOf(l)][indexOf(t)][indexOf(r)], 0, lhs, GRB.EQUAL, 0.0, "[C=PresIsNotInRoomAtTime][l" + l.getId() + ",t" + t.getId() + ",r" + r.getId() + "]");

                        if (t == firstTimeslot) {
                            getGrbModel().addConstr(roomSwitches[indexOf(l)][0], GRB.EQUAL, 0, "[C=FirstTimeslotSwitch][l" + l.getId() + ",t0]");
                            continue;
                        }

                        GRBLinExpr r1NotR2 = new GRBLinExpr();
                        // 7
                        //
                        //If you want 𝑥1≠𝑥2
                        //, you can linearize |𝑥1−𝑥2|≥𝜀, for example by introducing a boolean variable 𝑦=1 if and only if 𝑥1−𝑥2≥𝜀
                        //
                        //, and by imposing:
                        //
                        //𝑥1−𝑥2≤−𝜀+𝑀𝑦and𝑥1−𝑥2≥𝜀−(1−𝑦)𝑀
                        //
                        //Note: 𝜀
                        //is a "very small" constant close to zero and 𝑀 a very large integer.
                        r1NotR2.addTerm(1.0, lecturerHasPresAtTime[indexOf(l)][indexOf(t)][indexOf(r)]);
                        r1NotR2.addTerm(-1.0, lecturerHasPresAtTime[indexOf(l)][indexOf(t) - 1][indexOf(r)]);
                        getGrbModel().addGenConstrIndicator(roomSwitches[indexOf(l)][indexOf(t)], 1, r1NotR2, GRB.EQUAL, 1.0, "[C=IsARooSwitch][l" + l.getId() + ",t" + t.getId() + ",r" + r.getId() + "]");

                        getObjectives().addTerm(CostUtil.ROOM_SWITCH_COST, roomSwitches[indexOf(l)][indexOf(t)]);
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
