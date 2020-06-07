package ch.fhnw.ip6.originalsolver;

import ch.fhnw.ip6.api.AbstractSolver;
import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;

import ch.fhnw.ip6.solutionchecker.SolutionChecker;
import org.apache.commons.lang3.time.StopWatch;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.nary.alldifferent.conditions.Condition;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ch.fhnw.ip6.common.util.CostUtil.LECTURER_PER_LESSON_COST;
import static ch.fhnw.ip6.common.util.CostUtil.USED_ROOM_COST;

@Component("ch.fhnw.ip6.cpsolver.Solver")
public class Solver extends AbstractSolver {

    private ChocoModel cpModel;

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

        cpModel = new ChocoModel(presentations, lecturers, rooms, timeslots, offTimes, new Model());

        //Create cpModel.getModel() presTimeRoom[p,t,r] == 1 -> Presentation p happens in room r at time t
        IntVar[][] presRoomTime = cpModel.getY();

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

        // START CONSTRAINT: Each Lecturer can only have one presentation per time
        buildConstraintPresOnePresPerLecturerPerTime(presentationsPerLecturer,lecturers,presentations,rooms,timeslots,presRoomTime);

        // START CONSTRAINT:  For each Presentation, there must be 1 (room,timeslot) pair. -> Each presentation must be presented in a room at a time
        //buildConstraintPresScheduledAtRoomAtTime(presentations, rooms, timeslots, presRoomTime);
        // END CONSTRAINT

        // START CONSTRAINT For each (room, timeslot) pair there must be <=1 presentation -> Max 1 Presentation per Room/Time
        //buildConstraintMaxOnePresentationPerRoomTime(presentations, rooms, timeslots, presRoomTime);
        // END CONSTRAINT

        // START CONSTRAINT Foreach presentation, the following conflicting (presentation,room, time) pairs are not allowed -> Lecturers may not have more than one presentation at a time.
        //buildConstraintLecturerNotMoreThanOnePresAtTime(lecturers, rooms, timeslots, presRoomTime, presentationsPerLecturer);
        // END CONSTRAINT



        // START CONSTRAINT Soft constraint 1 Coaches should have as little free timeslots between presentations as possible.
        IntVar[] firstTimeslots = new IntVar[lecturers.size()];
        IntVar[] diffs = new IntVar[lecturers.size()];
        IntVar[] lastTimeslots = new IntVar[lecturers.size()];
        BoolVar[][] lecturerTimeslot = new BoolVar[lecturers.size()][timeslots.size()]; // Coach has a presentation at timeslot
        //buildConstraintMinFreeTimeslotsBetweenPresentations(lecturers, timeslots, rooms, presentations, presRoomTime, lecturerTimeslot, objIntVars, objIntCoeffs, firstTimeslots, diffs, lastTimeslots);
        // END CONSTRAINT

/*
        // START CONSTRAINT Soft Constraint 2 Coaches should switch the rooms as little as possible
        IntVar[][][] coachTimeRoomBool = new IntVar[lecturers.size()][timeslots.size()][rooms.size()];
        IntVar[][] coachRoomTime = new IntVar[lecturers.size()][timeslots.size()];
        IntVar[][] roomDiffsInt = new IntVar[lecturers.size()][timeslots.size()];
        IntVar[][] roomDiffsBool = new IntVar[lecturers.size()][timeslots.size()];
        IntVar[] numChangesForLecturer = new IntVar[lecturers.size()];
        buildConstraintMinRoomSwitches(lecturers, rooms, timeslots, presRoomTime, presentationsPerLecturer, objIntVars, objIntCoeffs, coachTimeRoomBool, coachRoomTime, roomDiffsInt, roomDiffsBool, numChangesForLecturer, lecturerTimeslot);
        // END CONSTRAINT
        */


        // START CONSTRAINT 3 As little rooms as possible should be free per timeslots -> Minimize used Timeslots
        int[] timeslotCost = new int[timeslots.size()];
        //buildConstraintMinUsedTimeslots(presentations, rooms, timeslots, presRoomTime, objIntVars, objIntCoeffs, timeslotCost);
        // END CONSTRAINT


        // START CONSTRAINT 4 As little rooms as possible should be used over all -> Minimize used Rooms over all timeslots
        //buildConstraintMinUsedRooms(presentations, rooms, timeslots, presRoomTime, objIntVars, objIntCoeffs);
        // END CONSTRAINT






        // Add the objective to the Solver, parse to array first because java is funny like that
        int[] objIntCoeffsArr = objIntCoeffs.stream().mapToInt(i -> i).toArray();
        IntVar[] objIntVarsArr = objIntVars.toArray(new IntVar[0]);
        IntVar OBJ = getModel().intVar("objective", 0, 100000);
        getModel().scalar(objIntVarsArr,objIntCoeffsArr,"+", OBJ).post();

        // finally, minimize the objective
        // TODO getModel().min(LinearExpr.scalProd(objIntVarsArr, objIntCoeffsArr));
        getModel().setObjective(Model.MINIMIZE, OBJ);

        org.chocosolver.solver.Solver solver = getModel().getSolver();
        solver.limitTime(timelimit * 1000); // In Milliseconds
        System.out.println("All constraints done, solving");

        solver.showShortStatistics();
        solver.printStatistics();
        ChocoCallback chocoCallback = new ChocoCallback();
         SolutionChecker solutionChecker = new SolutionChecker();

        while(solver.solve()){
            solver.showShortStatistics();
            chocoCallback.OnChocoCallback(getModel(), presRoomTime, presentations,rooms,timeslots,lecturers,solutionChecker);
        }

        // TODO System.out.println(getModel().validate());
        // TODO PresentationSolutionObserver cb = new PresentationSolutionObserver(presRoomTime, lecturers, presentations, timeslots, rooms, stopWatch, solverContext, coachRoomTime, roomDiffsInt, numChangesForLecturer);

//        CpSolverStatus res = solver.searchAllSolutions(getModel(), cb);
//        System.out.println(res);
//        solverContext.setSolving(false);
//        stopWatch.stop();
//        Planning p = solverContext.getPlanning();
//        p.setStatus(res.name());
        return null;
    }

    //             getModel().allDifferentUnderCondition(arr,EXCEPT_Minus1,true); // ALL DIFFERENT EXCEPT -1
    Condition EXCEPT_Minus1 = new Condition() {
        public boolean holdOnVar(IntVar x) {
            return !x.contains(-1);
        }
        public String toString() {
            return "_except_-1";
        }
    };

    // Lecturer may not have more than 1 presentation per timeslot -> Limit at most 1 room allowed per timeslot
    private void buildConstraintPresOnePresPerLecturerPerTime(List<P>[] presentationsPerLecturer,List<L> lecturers, List<P> presentations, List<R> rooms, List<T> timeslots, IntVar[][] presRoomTime) {
        for (L l: lecturers) {
            List<IntVar> temp = new ArrayList<>();
            List<BoolVar> tempBools = new ArrayList<>();

            for(P p: presentationsPerLecturer[l.getId()]){

                for (T t:timeslots) {
                    temp.add(presRoomTime[idx(p)][idx(t)]);
                }
            }
            temp.forEach(v -> {
                BoolVar bv = getModel().boolVar();
                getModel().ifThenElse(getModel().arithm(v, "=", -1), getModel().arithm(bv, "=", 0), getModel().arithm(bv, "=", 1));
                tempBools.add(bv);
            });


            BoolVar[] arr = tempBools.toArray(new BoolVar[tempBools.size()]);
            // max one may be != -1
            if(arr.length > 0){
                getModel().addClausesAtMostOne(arr);
            }
        }
    }

/*
        private void buildConstraintPresScheduledAtRoomAtTime(List<P> presentations, List<R> rooms, List<T> timeslots, IntVar[][] presRoomTime) {
        for (P p : presentations) {
            List<BoolVar> temp = new ArrayList<>();
            for (T t : timeslots) {
                for (R r : rooms) {
                    if (presRoomTime[idx(p)][idx(r)][idx(t)] == null) continue;

                    temp.add(presRoomTime[idx(p)][idx(r)][idx(t)]);
                }
            }
            BoolVar[] arr = temp.toArray(new BoolVar[0]);
            if(arr.length > 0) {
                getModel().sum(arr, "=", 1).post();
            }
            //getModel().addLinearConstraint(LinearExpr.sum(arr), 1, 1); // SUM OF ALL MUST EQUAL ONE
        }
    }

    private void buildConstraintMaxOnePresentationPerRoomTime(List<P> presentations, List<R> rooms, List<T> timeslots, IntVar[][] presRoomTime) {
        for (R r : rooms) {
            for (T t : timeslots) {
                List<BoolVar> temp = new ArrayList<>();
                for (P p : presentations) {
                    if (presRoomTime[idx(p)][idx(r)][idx(t)] == null) continue;
                    temp.add(presRoomTime[idx(p)][idx(r)][idx(t)]);
                }
                IntVar[] arr = temp.toArray(new BoolVar[0]);
                if(arr.length > 0) {

                    getModel().sum(arr, "<=", 1).post();
                }
                //getModel().addLinearConstraint(LinearExpr.sum(arr), 0, 1);
            }
        }
    }

    private void buildConstraintLecturerNotMoreThanOnePresAtTime(List<L> lecturers, List<R> rooms, List<T> timeslots, IntVar[][] presRoomTime, List<P>[] presentationsPerLecturer) {
        for (L l : lecturers) {
            for (T t : timeslots) {
                List<BoolVar> temp = new ArrayList<>();
                for (R r : rooms) {
                    for (P p1 : presentationsPerLecturer[idx(l)]) {
                        if (presRoomTime[idx(p1)][idx(r)][idx(t)] == null) continue;
                        temp.add(presRoomTime[idx(p1)][idx(r)][idx(t)]);
                    }
                }
                IntVar[] arr = temp.toArray(new BoolVar[0]);
                if(arr.length > 0) {
                    getModel().sum(arr, "<=", 1).post();
                }
            }
        }
    }

    private void buildConstraintMinFreeTimeslotsBetweenPresentations(List<L> lecturers, List<T> timeslots, List<R> rooms, List<P> presentations, BoolVar[][][] presRoomTime, BoolVar[][] lecturerTimeslot, ArrayList<IntVar> objIntVars, ArrayList<Integer> objIntCoeffs,
                                                                     IntVar[] firstTimeslots, IntVar[] diffs, IntVar[] lastTimeslots) {
        for (L l : lecturers) {
            for (T t : timeslots) {
                lecturerTimeslot[idx(l)][idx(t)] = getModel().boolVar("lecturerTimeslot_" + idx(l) + "_" + t.getId());
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
                if(temp.size() == 0){continue;} // No items in array, skip
                IntVar[] arr = temp.toArray(new IntVar[0]);
                getModel().ifThenElse(getModel().sum(arr, "<=",1), getModel().arithm(lecturerTimeslot[idx(l)][idx(t)], "=", 1),  getModel().arithm(lecturerTimeslot[idx(l)][idx(t)], "=", 0));
                // Implement lecturerTimeslot[c][t] == (sum(arr) >= 1)
                //getModel().addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(lecturerTimeslot[idx(l)][idx(t)]);
                //getModel().addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(lecturerTimeslot[idx(l)][idx(t)].not());
            }
        }
        for (L l : lecturers) { // Calculate first / last timeslot and difference per lecturer

            firstTimeslots[idx(l)] = Solver.this.getModel().intVar("firstTimeslot" + l.getId(), 0, timeslots.size() );

            lastTimeslots[idx(l)] = Solver.this.getModel().intVar("firstTimeslot" + l.getId(), 0, timeslots.size() );

            diffs[idx(l)] = Solver.this.getModel().intVar("diff" + l.getId(), 0, timeslots.size() );

            for (T t : timeslots) {
                this.getModel().ifThen(lecturerTimeslot[idx(l)][idx(t)], getModel().arithm(lastTimeslots[idx(l)], ">=", t.getId()));
                this.getModel().ifThen(lecturerTimeslot[idx(l)][idx(t)], getModel().arithm(firstTimeslots[idx(l)], "<=", t.getId()));
                //getModel().addGreaterOrEqual(lastTimeslots[idx(l)], t.getId()).onlyEnforceIf(lecturerTimeslot[idx(l)][idx(t)]);
                //getModel().addLessOrEqual(firstTimeslots[idx(l)], t.getId()).onlyEnforceIf(lecturerTimeslot[idx(l)][idx(t)]);
            }
            //LinearExpr diffExpr = LinearExpr.scalProd(new IntVar[]{lastTimeslots[idx(l)], firstTimeslots[idx(l)]}, new int[]{1, -1}); // Last timeslot - first timeslot
            getModel().arithm(lastTimeslots[idx(l)], "-", firstTimeslots[idx(l)], "=", diffs[idx(l)]).post();
            //getModel().addEquality(diffExpr, diffs[idx(l)]);


            //objIntVars.add(diffs[idx(l)]); // add it to the objective
            //objIntCoeffs.add(LECTURER_PER_LESSON_COST);


        }

    }

    private void buildConstraintMinRoomSwitches(List<L> lecturers, List<R> rooms, List<T> timeslots, IntVar[][][] presRoomTime, List<P>[] presentationsPerLecturer, ArrayList<IntVar> objIntVars, ArrayList<Integer> objIntCoeffs, IntVar[][][] coachTimeRoomBool, IntVar[][] coachRoomTime, IntVar[][] roomDiffsInt, IntVar[][] roomDiffsBool, IntVar[] numChangesForLecturer, IntVar[][] lecturerTimeslot) {
        // Variable / Array setup for all the things
        for (L l : lecturers) {
            for (T t : timeslots) {
                //coachRoomTime[idx(l)][idx(t)] = getModel().newIntVar(-1, rooms.size(), "coach_" + idx(l) + "time_" + t.getId()); // Number of room lecturer has at room/time
                //roomDiffsInt[idx(l)][idx(t)] = getModel().newIntVar(0, 200L, "coach_" + idx(l) + "time_" + t.getId()); // Room ID difference between presentations
                //roomDiffsBool[idx(l)][idx(t)] = getModel().newBoolVar("coach_" + idx(l) + "switchAt_time_" + t.getId()); // TRUE if coach switches rooms at time, FALSE if not
                for (R r : rooms) {
                    //  coachTimeRoomBool[idx(l)][idx(t)][idx(r)] = getModel().newBoolVar("coach_" + idx(l) + "time_" + idx(t) + "room_" + r.getId()); //Boolean if leturer has pres at room in time
                }
            }
            //numChangesForLecturer[idx(l)] = getModel().newIntVar(0, timeslots.size(), "numRoomChangesForLecturer" + l.getId()); //Number of changes for lecturer
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
//                    getModel().addEquality(LinearExpr.sum(arr), coachTimeRoomBool[idx(l)][idx(t)][idx(r)]); // same as above??

                    // Integer conversion -> coach has Room at Time = number
//                    getModel().addHint(coachRoomTime[idx(l)][idx(t)], -1);
//                    getModel().addEquality(coachRoomTime[idx(l)][idx(t)], r.getId()).onlyEnforceIf(coachTimeRoomBool[idx(l)][idx(t)][idx(r)]); // set value to roomid if lecturer has pres at this time
                }

                IntVar hasPresAtCurrTime = model.newBoolVar("hasPres"+l.getId()+"AtTime"+t.getId());
                model.addGreaterOrEqual(LinearExpr.sum(coachTimeRoomBool[l.getId()][t.getId()]),1).onlyEnforceIf(hasPresAtCurrTime);
                model.addLessOrEqual(LinearExpr.sum(coachTimeRoomBool[l.getId()][t.getId()]),0).onlyEnforceIf(hasPresAtCurrTime.not());
                model.addEquality(coachRoomTime[l.getId()][t.getId()], -1).onlyEnforceIf(hasPresAtCurrTime.not());// set value -1 if lecturer has no presentation at this time



                //Set no room (-1) if the lecturer doesnt have a pres at this time
                //getModel().addEquality(coachRoomTime[idx(l)][idx(t)], -1).onlyEnforceIf(lecturerTimeslot[idx(l)][idx(t)].not());


            }


            for (T t : timeslots) {
                if (idx(t) == 0) {
                    //getModel().addEquality(roomDiffsInt[idx(l)][0], 0); // difference between the 0 index of array is 0 because there wasnt a presentation before
                    continue;
                }
                // skip the first presentation as there cant be a earlier presentation
                //LinearExpr diffExpr = LinearExpr.scalProd(new IntVar[]{coachRoomTime[idx(l)][idx(t)], coachRoomTime[idx(l)][idx(t) - 1]}, new int[]{1, -1}); // current timeslot room ID - previous timeslot room ID


                //IntVar absDiffInt = getModel().newIntVar(-100L, 100L, "DiffInt_l" + idx(l) + "t_" + t.getId());
                //getModel().addEquality(diffExpr, absDiffInt);
                //getModel().addAbsEquality(roomDiffsInt[idx(l)][idx(t)], absDiffInt);

                // if difference is greaterEqual than 1, switch is true
                //getModel().addGreaterOrEqual(roomDiffsInt[idx(l)][idx(t)], 1).onlyEnforceIf(roomDiffsBool[idx(l)][idx(t)]);
                //getModel().addLessOrEqual(roomDiffsInt[idx(l)][idx(t)], 0).onlyEnforceIf(roomDiffsBool[idx(l)][idx(t)].not());
            }


            // Problem here somewhere
            //numChangesForLecturer[idx(l)] = getModel().newIntVar(0, timeslots.size(), "numRoomChangesForLecturer" + l.getId()); //Number of changes for lecturer is sum of changed booleans
            //getModel().addEquality(numChangesForLecturer[idx(l)], LinearExpr.sum(roomDiffsBool[idx(l)])); // Add the equality

            // finally, add the objective
            //objIntVars.add(numChangesForLecturer[l.getId()]);
            //objIntCoeffs.add(ROOM_SWITCH_COST);
        }
    }

    private void buildConstraintMinUsedTimeslots(List<P> presentations, List<R> rooms, List<T> timeslots, IntVar[][][] presRoomTime, ArrayList<IntVar> objIntVars, ArrayList<Integer> objIntCoeffs, int[] timeslotCost) {
        IntVar[] timeslotUsed = new IntVar[timeslots.size()];
        for (T t : timeslots) {
            timeslotUsed[idx(t)] = getModel().boolVar("timeslotUsed_" + t.getId());
        }

        for (T t : timeslots) {
            List<IntVar> temp = new ArrayList<IntVar>();

            for (R r : rooms) {
                for (P p : presentations) {
                    if (presRoomTime[idx(p)][idx(r)][idx(t)] == null) continue;
                    temp.add(presRoomTime[idx(p)][idx(r)][idx(t)]);
                }
            }
            BoolVar[] arr = temp.toArray(new BoolVar[0]);
            /// IF SUM ARR > 0 add boolean timeslotUsed TRUE else FALSE;
            // Implement timeslotUsed[t] == (sum(arr) >= 1).
            getModel().ifThenElse(getModel().sum(arr, ">=",1), getModel().arithm(timeslotUsed[idx(t)], "=", 1) , getModel().arithm(timeslotUsed[idx(t)], "=", 0));
            //getModel().addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(timeslotUsed[idx(t)]);
            //getModel().addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(timeslotUsed[idx(t)].not());

            //Add Objective
            //objIntVars.add(timeslotUsed[idx(t)]);
            //objIntCoeffs.add(t.getPriority());
        }
    }

    private void buildConstraintMinUsedRooms(List<P> presentations, List<R> rooms, List<T> timeslots, IntVar[][][] presRoomTime, ArrayList<IntVar> objIntVars, ArrayList<Integer> objIntCoeffs) {
        IntVar[] roomUsed = new IntVar[rooms.size()];
        for (R r : rooms) {
            roomUsed[idx(r)] = getModel().boolVar("roomUsed_" + r.getId());
        }

        for (R r : rooms) {
            List<IntVar> temp = new ArrayList<>();

            for (T t : timeslots) {
                for (P p : presentations) {
                    if (presRoomTime[idx(p)][idx(r)][idx(t)] == null) continue;
                    temp.add(presRoomTime[idx(p)][idx(r)][idx(t)]);
                }
            }
            if(temp.size() == 0){
                continue;
            }
            BoolVar[] arr = temp.toArray(new BoolVar[0]);
            getModel().ifThenElse(getModel().sum(arr, ">=",1), getModel().arithm(roomUsed[idx(r)], "=", 1) , getModel().arithm(roomUsed[idx(r)], "=", 0));

            //Add Objective
            objIntVars.add(roomUsed[idx(r)]);
            objIntCoeffs.add(USED_ROOM_COST);
        }
    }
    */

    private Model getModel() {
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
