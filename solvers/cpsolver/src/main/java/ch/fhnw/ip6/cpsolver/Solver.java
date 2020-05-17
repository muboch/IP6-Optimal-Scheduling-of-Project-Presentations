package ch.fhnw.ip6.cpsolver;

import ch.fhnw.ip6.api.AbstractSolver;
import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ch.fhnw.ip6.common.util.CostUtil.*;

@Component("ch.fhnw.ip6.cpsolver.Solver")
public class Solver extends AbstractSolver {

    static {
        System.loadLibrary("jniortools");
    }

    private CPModel cpModel;

    public Solver(SolverContext solverContext) {
        super(solverContext);
    }

    @Override
    public Planning solve(List<P> presentations, List<L> lecturers, List<R> rooms, List<T> timeslots, boolean[][] offTimes) {
        solverContext.setSolving(true);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        presentations.forEach(System.out::println);
        rooms.forEach(System.out::println);
        timeslots.forEach(System.out::println);
        lecturers.forEach(System.out::println);

        cpModel = new CPModel(presentations, lecturers, rooms, timeslots, offTimes, new CpModel());

        //Create cpModel.getModel() presTimeRoom[p,t,r] == 1 -> Presentation p happens in room r at time t
        IntVar[][][] presRoomTime = cpModel.getPresRoomTime();

        System.out.println("Setup completed");
        // For each lecturer, list the presentations that are not allowed to overlap
        List<P>[] presentationsPerLecturer = new ArrayList[lecturers.size()];
        for (L l : lecturers) {
            presentationsPerLecturer[idx(l)] = presentations.stream().filter(ps -> ps.getExpert().getId() == l.getId() || ps.getCoach().getId() == l.getId()).collect(Collectors.toList());
        }
        System.out.println("Overlap calculation completed");

        // Data structures for Objectives
        ArrayList<IntVar> objIntVars = new ArrayList<>();
        ArrayList<Integer> objIntCoeffs = new ArrayList<>();


        // START CONSTRAINT:  For each Presentation, there must be 1 (room,timeslot) pair. -> Each presentation must be presented in a room at a time
        buildConstraintPresScheduledAtRoomAtTime(presentations, rooms, timeslots, presRoomTime);
        // END CONSTRAINT

        // START CONSTRAINT For each (room, timeslot) pair there must be <=1 presentation -> Max 1 Presentation per Room/Time
        buildConstraintMaxOnePresentationPerRoomTime(presentations, rooms, timeslots, presRoomTime);
        // END CONSTRAINT

        // START CONSTRAINT Foreach presentation, the following conflicting (presentation,room, time) pairs are not allowed -> Lecturers may not have more than one presentation at a time.
        buildConstraintLecturerNotMoreThanOnePresAtTime(lecturers, rooms, timeslots, presRoomTime, presentationsPerLecturer);
        // END CONSTRAINT

        // START CONSTRAINT 1. Coaches should have as little free timeslots between presentations as possible.
        IntVar[] firstTimeslots = new IntVar[lecturers.size()];
        IntVar[] diffs = new IntVar[lecturers.size()];
        IntVar[] lastTimeslots = new IntVar[lecturers.size()];
        IntVar[][] lecturerTimeslot = new IntVar[lecturers.size()][timeslots.size()]; // Coach has a presentation at timeslot
        int[] timeslotCost = new int[timeslots.size()];
        buildConstraintMinFreeTimeslotsBetweenPresentations(lecturers, timeslots, rooms, presentations, presRoomTime, timeslotCost, lecturerTimeslot, objIntVars, objIntCoeffs, firstTimeslots, diffs, lastTimeslots);
        // END CONSTRAINT

        // START CONSTRAINT Soft Constraint 1.1 Coaches should switch the rooms as little as possible
        IntVar[][][] coachTimeRoomBool = new IntVar[lecturers.size()][timeslots.size()][rooms.size()];
        IntVar[][] coachRoomTime = new IntVar[lecturers.size()][timeslots.size()];
        IntVar[][] roomDiffsInt = new IntVar[lecturers.size()][timeslots.size()];
        IntVar[][] roomDiffsBool = new IntVar[lecturers.size()][timeslots.size()];
        IntVar[] numChangesForLecturer = new IntVar[lecturers.size()];
        buildConstraintMinRoomSwitches(lecturers, rooms, timeslots, presRoomTime, presentationsPerLecturer, objIntVars, objIntCoeffs, coachTimeRoomBool, coachRoomTime, roomDiffsInt, roomDiffsBool, numChangesForLecturer, lecturerTimeslot);
        // END CONSTRAINT


        // START CONSTRAINT 3.1 As little rooms as possible should be free per timeslots -> Minimize used Timeslots
        buildConstraintMinUsedTimeslots(presentations, rooms, timeslots, presRoomTime, objIntVars, objIntCoeffs, timeslotCost);
        // END CONSTRAINT

        // START CONSTRAINT 4 As little rooms as possible should be used over all -> Minimize used Rooms over all timeslots
        buildConstraintMinUsedRooms(presentations, rooms, timeslots, presRoomTime, objIntVars, objIntCoeffs);
        // END CONSTRAINT


        // Add the objective to the Solver, parse to array first because java is funny like that
        int[] objIntCoeffsArr = objIntCoeffs.stream().mapToInt(i -> i).toArray();
        IntVar[] objIntVarsArr = objIntVars.toArray(new IntVar[0]);

        // finally, minimize the objective
        getModel().minimize(LinearExpr.scalProd(objIntVarsArr, objIntCoeffsArr));

        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(timelimit);
        System.out.println("All constraints done, solving");
        System.out.println(getModel().validate());
        PresentationSolutionObserver cb = new PresentationSolutionObserver(presRoomTime, lecturers, presentations, timeslots, rooms, stopWatch, solverContext, coachRoomTime, roomDiffsInt, numChangesForLecturer);

        CpSolverStatus res = solver.searchAllSolutions(getModel(), cb);
        System.out.println(res);
        solverContext.setSolving(false);
        stopWatch.stop();
        Planning p = solverContext.getPlanning();
        p.setStatus(res.name());
        return p;
    }

    private void buildConstraintPresScheduledAtRoomAtTime(List<P> presentations, List<R> rooms, List<T> timeslots, IntVar[][][] presRoomTime) {
        for (P p : presentations) {
            List<IntVar> temp = new ArrayList<>();
            for (T t : timeslots) {
                for (R r : rooms) {
                    if (presRoomTime[idx(p)][idx(r)][idx(t)] == null) continue;

                    temp.add(presRoomTime[idx(p)][idx(r)][idx(t)]);
                }
            }
            IntVar[] arr = temp.toArray(new IntVar[0]);
            getModel().addLinearConstraint(LinearExpr.sum(arr), 1, 1); // SUM OF ALL MUST EQUAL ONE
        }
    }

    private void buildConstraintMaxOnePresentationPerRoomTime(List<P> presentations, List<R> rooms, List<T> timeslots, IntVar[][][] presRoomTime) {
        for (R r : rooms) {
            for (T t : timeslots) {
                List<IntVar> temp = new ArrayList<>();
                for (P p : presentations) {
                    if (presRoomTime[idx(p)][idx(r)][idx(t)] == null) continue;
                    temp.add(presRoomTime[idx(p)][idx(r)][idx(t)]);
                }
                IntVar[] arr = temp.toArray(new IntVar[0]);
                getModel().addLinearConstraint(LinearExpr.sum(arr), 0, 1);
            }
        }
    }

    private void buildConstraintLecturerNotMoreThanOnePresAtTime(List<L> lecturers, List<R> rooms, List<T> timeslots, IntVar[][][] presRoomTime, List<P>[] presentationsPerLecturer) {
        for (L l : lecturers) {
            for (T t : timeslots) {
                List<IntVar> temp = new ArrayList<>();
                for (R r : rooms) {
                    for (P p1 : presentationsPerLecturer[idx(l)]) {
                        if (presRoomTime[idx(p1)][idx(r)][idx(t)] == null) continue;
                        temp.add(presRoomTime[idx(p1)][idx(r)][idx(t)]);
                    }
                }
                IntVar[] arr = temp.toArray(new IntVar[0]);
                getModel().addLinearConstraint(LinearExpr.sum(arr), 0, 1); // <=1 -> max one out of overlap is allowed
            }
        }
    }

    private void buildConstraintMinFreeTimeslotsBetweenPresentations(List<L> lecturers, List<T> timeslots, List<R> rooms, List<P> presentations, IntVar[][][] presRoomTime, int[] timeslotCost, IntVar[][] lecturerTimeslot, ArrayList<IntVar> objIntVars, ArrayList<Integer> objIntCoeffs,
                                                                     IntVar[] firstTimeslots, IntVar[] diffs, IntVar[] lastTimeslots) {
        for (L l : lecturers) {
            for (T t : timeslots) {
                lecturerTimeslot[idx(l)][idx(t)] = getModel().newBoolVar("lecturerTimeslot_" + idx(l) + "_" + t.getId());

                timeslotCost[idx(t)] = t.getPriority();
                ArrayList<IntVar> temp = new ArrayList<>();

                for (R r : rooms) {
                    for (P p : presentations) {
                        if (!(p.getExpert().getId() == idx(l) || p.getCoach().getId() == l.getId())) {
                            continue;
                        } // If lecturer is not coach or expert for this presentation, skip the presentation
                        if (presRoomTime[idx(p)][idx(r)][idx(t)] == null) continue;
                        temp.add(presRoomTime[idx(p)][idx(r)][idx(t)]);
                    }
                }
                IntVar[] arr = temp.toArray(new IntVar[0]);
                // Implement lecturerTimeslot[c][t] == (sum(arr) >= 1)
                getModel().addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(lecturerTimeslot[idx(l)][idx(t)]);
                getModel().addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(lecturerTimeslot[idx(l)][idx(t)].not());
            }
        }
        for (L l : lecturers) { // Calculate first / last timeslot and difference per lecturer
            firstTimeslots[idx(l)] = Solver.this.getModel().newIntVar(0, timeslots.size(), "firstTimeslot" + l.getId());
            lastTimeslots[idx(l)] = getModel().newIntVar(0, timeslots.size(), "lastTimeslot" + l.getId());
            diffs[idx(l)] = getModel().newIntVar(0, timeslots.size(), "diff_" + l.getId());

            for (T t : timeslots) {
                getModel().addGreaterOrEqual(lastTimeslots[idx(l)], t.getId()).onlyEnforceIf(lecturerTimeslot[idx(l)][idx(t)]);
                getModel().addLessOrEqual(firstTimeslots[idx(l)], t.getId()).onlyEnforceIf(lecturerTimeslot[idx(l)][idx(t)]);
            }
            LinearExpr diffExpr = LinearExpr.scalProd(new IntVar[]{lastTimeslots[idx(l)], firstTimeslots[idx(l)]}, new int[]{1, -1}); // Last timeslot - first timeslot
            getModel().addEquality(diffExpr, diffs[idx(l)]);
            objIntVars.add(diffs[idx(l)]); // add it to the objective
            objIntCoeffs.add(LECTURER_PER_LESSON_COST);
        }

    }

    private void buildConstraintMinRoomSwitches(List<L> lecturers, List<R> rooms, List<T> timeslots, IntVar[][][] presRoomTime, List<P>[] presentationsPerLecturer, ArrayList<IntVar> objIntVars, ArrayList<Integer> objIntCoeffs, IntVar[][][] coachTimeRoomBool, IntVar[][] coachRoomTime, IntVar[][] roomDiffsInt, IntVar[][] roomDiffsBool, IntVar[] numChangesForLecturer, IntVar[][] lecturerTimeslot) {
        // Variable / Array setup for all the things
        for (L l : lecturers) {
            for (T t : timeslots) {
                coachRoomTime[idx(l)][idx(t)] = getModel().newIntVar(-1, rooms.size(), "coach_" + idx(l) + "time_" + t.getId()); // Number of room lecturer has at room/time
                roomDiffsInt[idx(l)][idx(t)] = getModel().newIntVar(0, 200L, "coach_" + idx(l) + "time_" + t.getId()); // Room ID difference between presentations
                roomDiffsBool[idx(l)][idx(t)] = getModel().newBoolVar("coach_" + idx(l) + "switchAt_time_" + t.getId()); // TRUE if coach switches rooms at time, FALSE if not
                for (R r : rooms) {
                    coachTimeRoomBool[idx(l)][idx(t)][idx(r)] = getModel().newBoolVar("coach_" + idx(l) + "time_" + idx(t) + "room_" + r.getId()); //Boolean if leturer has pres at room in time
                }
            }
            numChangesForLecturer[idx(l)] = getModel().newIntVar(0, timeslots.size(), "numRoomChangesForLecturer" + l.getId()); //Number of changes for lecturer
        }

        for (L l : lecturers) {
            for (T t : timeslots) {
                for (R r : rooms) {
                    List<IntVar> temp = new ArrayList<>();
                    for (P p1 : presentationsPerLecturer[idx(l)]) {
                        if (presRoomTime[idx(p1)][idx(r)][idx(t)] == null) continue;
                        temp.add(presRoomTime[idx(p1)][idx(r)][idx(t)]);
                    }
                    IntVar[] arr = temp.toArray(new IntVar[0]);
                    // If a presentation is happening in room at time, true, else false.
                    //model.addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(coachTimeRoomBool[l.getId()][t.getId()][r.getId()]);
                    //model.addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(coachTimeRoomBool[l.getId()][t.getId()][r.getId()].not());
                    //model.addEquality(coachTimeRoomBool[l.getId()][t.getId()][r.getId()],0).onlyEnforceIf(lecturerTimeslot[l.getId()][t.getId()].not());
                    getModel().addEquality(LinearExpr.sum(arr), coachTimeRoomBool[idx(l)][idx(t)][idx(r)]); // same as above??

                    // Integer conversion -> coach has Room at Time = number
                    getModel().addHint(coachRoomTime[idx(l)][idx(t)], -1);
                    getModel().addEquality(coachRoomTime[idx(l)][idx(t)], r.getId()).onlyEnforceIf(coachTimeRoomBool[idx(l)][idx(t)][idx(r)]); // set value to roomid if lecturer has pres at this time
                }
                /*
                IntVar hasPresAtCurrTime = model.newBoolVar("hasPres"+l.getId()+"AtTime"+t.getId());
                model.addGreaterOrEqual(LinearExpr.sum(coachTimeRoomBool[l.getId()][t.getId()]),1).onlyEnforceIf(hasPresAtCurrTime);
                model.addLessOrEqual(LinearExpr.sum(coachTimeRoomBool[l.getId()][t.getId()]),0).onlyEnforceIf(hasPresAtCurrTime.not());
                model.addEquality(coachRoomTime[l.getId()][t.getId()], -1).onlyEnforceIf(hasPresAtCurrTime.not());// set value -1 if lecturer has no presentation at this time
                   */


                //Set no room (-1) if the lecturer doesnt have a pres at this time
                getModel().addEquality(coachRoomTime[idx(l)][idx(t)], -1).onlyEnforceIf(lecturerTimeslot[idx(l)][idx(t)].not());


            }


            for (T t : timeslots) {
                if (idx(t) == 0) {
                    getModel().addEquality(roomDiffsInt[idx(l)][0], 0); // difference between the 0 index of array is 0 because there wasnt a presentation before
                    continue;
                }
                // skip the first presentation as there cant be a earlier presentation
                LinearExpr diffExpr = LinearExpr.scalProd(new IntVar[]{coachRoomTime[idx(l)][idx(t)], coachRoomTime[idx(l)][idx(t) - 1]}, new int[]{1, -1}); // current timeslot room ID - previous timeslot room ID


                IntVar absDiffInt = getModel().newIntVar(-100L, 100L, "DiffInt_l" + idx(l) + "t_" + t.getId());
                getModel().addEquality(diffExpr, absDiffInt);
                getModel().addAbsEquality(roomDiffsInt[idx(l)][idx(t)], absDiffInt);

                // if difference is greaterEqual than 1, switch is true
                getModel().addGreaterOrEqual(roomDiffsInt[idx(l)][idx(t)], 1).onlyEnforceIf(roomDiffsBool[idx(l)][idx(t)]);
                getModel().addLessOrEqual(roomDiffsInt[idx(l)][idx(t)], 0).onlyEnforceIf(roomDiffsBool[idx(l)][idx(t)].not());
            }


            // Problem here somewhere
            numChangesForLecturer[idx(l)] = getModel().newIntVar(0, timeslots.size(), "numRoomChangesForLecturer" + l.getId()); //Number of changes for lecturer is sum of changed booleans
            getModel().addEquality(numChangesForLecturer[idx(l)], LinearExpr.sum(roomDiffsBool[idx(l)])); // Add the equality

            // finally, add the objective
            //objIntVars.add(numChangesForLecturer[l.getId()]);
            //objIntCoeffs.add(ROOM_SWITCH_COST);
        }
    }

    private void buildConstraintMinUsedTimeslots(List<P> presentations, List<R> rooms, List<T> timeslots, IntVar[][][] presRoomTime, ArrayList<IntVar> objIntVars, ArrayList<Integer> objIntCoeffs, int[] timeslotCost) {
        IntVar[] timeslotUsed = new IntVar[timeslots.size()];
        for (T t : timeslots) {
            timeslotUsed[idx(t)] = getModel().newBoolVar("timeslotUsed_" + t.getId());
        }

        for (T t : timeslots) {
            List<IntVar> temp = new ArrayList<IntVar>();

            for (R r : rooms) {
                for (P p : presentations) {
                    if (presRoomTime[idx(p)][idx(r)][idx(t)] == null) continue;
                    temp.add(presRoomTime[idx(p)][idx(r)][idx(t)]);
                }
            }
            IntVar[] arr = temp.toArray(new IntVar[0]);
            /// IF SUM ARR > 0 add boolean timeslotUsed TRUE else FALSE;
            // Implement timeslotUsed[t] == (sum(arr) >= 1).
            getModel().addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(timeslotUsed[idx(t)]);
            getModel().addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(timeslotUsed[idx(t)].not());

            //Add Objective
            objIntVars.add(timeslotUsed[idx(t)]);
            objIntCoeffs.add(timeslotCost[idx(t)]);
        }
    }

    private void buildConstraintMinUsedRooms(List<P> presentations, List<R> rooms, List<T> timeslots, IntVar[][][] presRoomTime, ArrayList<IntVar> objIntVars, ArrayList<Integer> objIntCoeffs) {
        IntVar[] roomUsed = new IntVar[rooms.size()];
        int[] roomCost = new int[rooms.size()];
        for (R r : rooms) {
            roomUsed[idx(r)] = getModel().newBoolVar("roomUsed_" + r.getId());
            roomCost[idx(r)] = USED_ROOM_COST;
        }

        for (R r : rooms) {
            List<IntVar> temp = new ArrayList<IntVar>();

            for (T t : timeslots) {
                for (P p : presentations) {
                    if (presRoomTime[idx(p)][idx(r)][idx(t)] == null) continue;
                    temp.add(presRoomTime[idx(p)][idx(r)][idx(t)]);
                }
            }
            IntVar[] arr = temp.toArray(new IntVar[0]);
            /// IF SUM ARR > 0 add boolean roomUsed TRUE else FALSE;
            // Implement timeslotUsed[t] == (sum(arr) >= 1).
            // If A then B
            getModel().addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(roomUsed[idx(r)]);
            getModel().addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(roomUsed[idx(r)].not());

            //Add Objective
            objIntVars.add(roomUsed[idx(r)]);
            objIntCoeffs.add(roomCost[idx(r)]);
        }
    }

    private CpModel getModel() {
        return cpModel.getModel();
    }

    private int idx(R r) {
        return cpModel.indexOf(r);
    }

    private int idx(L l) {
        return cpModel.indexOf(l);
    }

    private int idx(P p) {
        return cpModel.indexOf(p);
    }

    private int idx(T t) {
        return cpModel.indexOf(t);
    }
}
