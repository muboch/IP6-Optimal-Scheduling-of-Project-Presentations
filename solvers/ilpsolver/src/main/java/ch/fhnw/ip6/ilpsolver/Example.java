package ch.fhnw.ip6.ilpsolver;

import gurobi.GRB;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Example {

    static class P {
        P(String p, String l1, String l2) {
            this.p = p;
            this.l1 = l1;
            this.l2 = l2;
        }

        String p;
        String l1;
        String l2;
    }

    public static void main(String[] args) {
        try {

            String timeslots[] = new String[]{"T1", "T2", "T3", "T4", "T5"};
            P presentations[] = new P[]{new P("P1", "aaa", "bbb"), new P("P2", "eee", "fff"), new P("P3", "ccc", "ddd"),
                    new P("P4", "ddd", "eee"), new P("P5", "aaa", "fff"), new P("P6", "ccc", "bbb"), new P("P7", "eee", "aaa")};
            String rooms[] = new String[]{"R1", "R2", "R3", "R4"};
            String lecturers[] = new String[]{"aaa", "bbb", "ccc", "ddd", "eee", "fff"};


            List<P>[] presentationsPerLecturer = new ArrayList[lecturers.length];
            for (int l = 0; l < lecturers.length; ++l) {
                int finalL = l;
                List<P> presPerL = Arrays.stream(presentations).filter(p -> p.l1.equals(lecturers[finalL]) || p.l2.equals(lecturers[finalL])).collect(Collectors.toList());
                presentationsPerLecturer[l] = presPerL;
            }

            int nTimeslots = timeslots.length;
            int nPresentations = presentations.length;
            int nRooms = rooms.length;
            int nLecturers = lecturers.length;

            // Model
            GRBEnv env = new GRBEnv();
            GRBModel model = new GRBModel(env);
            model.set(GRB.StringAttr.ModelName, "assignment");

            GRBVar[][][] x = new GRBVar[nPresentations][nTimeslots][nRooms];
            for (int p = 0; p < nPresentations; ++p) {
                for (int t = 0; t < nTimeslots; ++t) {
                    for (int r = 0; r < nRooms; ++r) {
                        x[p][t][r] = model.addVar(0, 1, 1.0, GRB.BINARY, presentations[p].p + "[" + presentations[p].l1 + ":" + presentations[p].l2 + "]" + "." + timeslots[t] + "." + rooms[r]);
                    }
                }
            }

            // CONSTRAINT: presentation in one room and one timeslot
            for (int p = 0; p < nPresentations; ++p) {
                GRBLinExpr lhs = new GRBLinExpr();
                for (int t = 0; t < nTimeslots; ++t) {
                    for (int r = 0; r < nRooms; ++r) {
                        lhs.addTerm(1.0, x[p][t][r]);
                    }
                }
                model.addConstr(lhs, GRB.EQUAL, 1.0, presentations[p].p);
            }
            // END

            // CONSTRAINT: only one presentation per room and timeslot
            for (int t = 0; t < nTimeslots; ++t) {
                for (int r = 0; r < nRooms; ++r) {
                    GRBLinExpr lhs = new GRBLinExpr();
                    for (int p = 0; p < nPresentations; ++p) {
                        lhs.addTerm(1.0, x[p][t][r]);
                    }
                    model.addConstr(lhs, GRB.LESS_EQUAL, 1.0, timeslots[t] + "." + rooms[r]);
                }
            }
            // END

            // START CONSTRAINT Foreach presentation, the following conflicting (presentation,room, time) pairs are not allowed -> Lecturers may not have more than one presentation at a time.
            for (int l = 0; l < nLecturers; ++l) {
                for (int t = 0; t < nTimeslots; ++t) {
                    GRBLinExpr lhs = new GRBLinExpr();
                    for (int r = 0; r < nRooms; ++r) {
                        for (int p = 0; p < presentationsPerLecturer[l].size(); ++p) {
                            int idxP = List.of(presentations).indexOf(presentationsPerLecturer[l].get(p));
                            lhs.addTerm(1.0, x[idxP][t][r]);
                        }
                    }
                    model.addConstr(lhs, GRB.LESS_EQUAL, 1.0, lecturers[l] + "." + timeslots[t]);
                }
            }
            // END
            model.update();

            // Optimize
            model.optimize();


            System.out.println("Solution found, objective = " + model.get(GRB.DoubleAttr.ObjVal));

            String[][][] vnames = model.get(GRB.StringAttr.VarName, x);
            double[][][] xd = model.get(GRB.DoubleAttr.X, x);

            for (int p = 0; p < nPresentations; p++) {
                for (int t = 0; t < nTimeslots; t++) {
                    for (int r = 0; r < nRooms; r++) {
                        if (xd[p][t][r] != 0.0) System.out.println(vnames[p][t][r] + " " + xd[p][t][r]);
                    }
                }
            }


            // Dispose of model and environment
            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
        }
    }
}