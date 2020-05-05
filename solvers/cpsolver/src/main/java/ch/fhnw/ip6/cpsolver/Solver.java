package ch.fhnw.ip6.cpsolver;

import ch.fhnw.ip6.api.AbstractSolver;
import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Timeslot;
import ch.fhnw.ip6.common.util.JsonUtil;
import com.google.ortools.sat.*;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ch.fhnw.ip6.common.util.CostUtil.*;

@Component("ch.fhnw.ip6.cpsolver.Solver")
public class Solver extends AbstractSolver {

    @Value("${ospp.timelimit}")
    private int timelimit = 3600;

    static {
        System.loadLibrary("jniortools");
    }

    public Solver(SolverContext solverContext) {
        super(solverContext);
    }

    @Override
    public Planning testSolve() {
        JsonUtil util = new JsonUtil();

        List<Presentation> presentations = util.getJsonAsList("presentations.json", Presentation.class);
        List<Lecturer> lecturers = util.getJsonAsList("lecturers.json", Lecturer.class);
        List<Room> rooms = util.getJsonAsList("rooms.json", Room.class).stream().filter(r -> r.getReserve().equals(false)).collect(Collectors.toList());
        List<Timeslot> timeslots = util.getJsonAsList("timeslots.json", Timeslot.class);

        for (Presentation p : presentations) {
            p.setCoach(lecturers.stream().filter(t -> t.getInitials().equals(p.getCoachInitials())).findFirst().get()); // Assign Coaches to Presentation
            p.setExpert(lecturers.stream().filter(t -> t.getInitials().equals(p.getExpertInitials())).findFirst().get()); // Assign Experts to Presentation
        }

        return solve(presentations, lecturers, rooms, timeslots, new boolean[lecturers.size()][timeslots.size()]);
    }

    @Override
    public Planning solve(List<Presentation> presentations, List<Lecturer> lecturers, List<Room> rooms, List<Timeslot> timeslots, boolean[][] locktimes) {
        solverContext.setSolving(true);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CpModel model = new CpModel();

        presentations.forEach(System.out::println);
        rooms.forEach(System.out::println);
        timeslots.forEach(System.out::println);
        lecturers.forEach(System.out::println);

        for (Presentation p : presentations) {
            p.setCoach(lecturers.stream().filter(t -> t.getInitials().equals(p.getCoachInitials())).findFirst().get()); // Assign Coaches to Presentation
            p.setExpert(lecturers.stream().filter(t -> t.getInitials().equals(p.getExpertInitials())).findFirst().get()); // Assign Experts to Presentation
        }

        //Create model. presTimeRoom[p,t,r] == 1 -> Presentation p happens in room r at time t
        IntVar[][][] presRoomTime = new IntVar[presentations.size()][rooms.size()][timeslots.size()];
        for (Timeslot t : timeslots) {
            for (Room r : rooms) {
                for (Presentation p : presentations) {
                    if (!p.getType().equals(r.getType())) { // If roomtype doesnt fit
                        continue;
                    }
                    if (locktimes[p.getCoach().getId()][t.getId()] || locktimes[p.getExpert().getId()][t.getId()]) { // If coach is locked at this time
                        continue;
                    }
                    presRoomTime[p.getId()][r.getId()][t.getId()] = model.newBoolVar("presRoomTime_p" + p.getId() + "_r" + r.getId() + "_t" + t.getId());
                }
            }
        }

        System.out.println("Setup completed");
        // For each lecturer, list the presentations that are not allowed to overlap
        List<Presentation>[] presentationsPerLecturer = new ArrayList[lecturers.size()];
        for (Lecturer l : lecturers) {
            presentationsPerLecturer[l.getId()] = presentations.stream().filter(ps -> ps.getExpert().getId() == l.getId() || ps.getCoach().getId() == l.getId()).collect(Collectors.toList());
        }
        System.out.println("Overlap calculation completed");

        // Data structures for Objectives
        ArrayList<IntVar> objIntVars = new ArrayList<IntVar>();
        ArrayList<Integer> objIntCoeffs = new ArrayList<>();


        // START CONSTRAINT:  For each Presentation, there must be 1 (room,timeslot) pair. -> Each presentation must be presented in a room at a time
        for (Presentation p : presentations) {
            List<IntVar> temp = new ArrayList<>();
            for (Timeslot t : timeslots) {
                for (Room r : rooms) {
                    if (presRoomTime[p.getId()][r.getId()][t.getId()] == null) continue;

                    temp.add(presRoomTime[p.getId()][r.getId()][t.getId()]);
                }
            }
            IntVar[] arr = temp.toArray(new IntVar[0]);
            // next line same as c#: "model.Add(LinearExpr.Sum(temp) == 1);"
            model.addLinearConstraint(LinearExpr.sum(arr), 1, 1); // SUM OF ALL MUST EQUAL ONE
        }
        // END CONSTRAINT

        // START CONSTRAINT For each (room, timeslot) pair there must be <=1 presentation -> Max 1 Presentation per Room/Time
        for (Room r : rooms) {
            for (Timeslot t : timeslots) {
                List<IntVar> temp = new ArrayList<IntVar>();
                for (Presentation p : presentations) {
                    if (presRoomTime[p.getId()][r.getId()][t.getId()] == null) continue;
                    temp.add(presRoomTime[p.getId()][r.getId()][t.getId()]);
                }
                IntVar[] arr = temp.toArray(new IntVar[0]);
                model.addLinearConstraint(LinearExpr.sum(arr), 0, 1);
            }
        }
        // END CONSTRAINT


        // START CONSTRAINT Foreach presentation, the following conflicting (presentation,room, time) pairs are not allowed -> Lecturers may not have more than one presentation at a time.
        for (Lecturer l : lecturers) {
            for (Timeslot t : timeslots) {
                List<IntVar> temp = new ArrayList<>();
                for (Room r : rooms) {
                    for (Presentation p1 : presentationsPerLecturer[l.getId()]) {
                        if (presRoomTime[p1.getId()][r.getId()][t.getId()] == null) continue;
                        temp.add(presRoomTime[p1.getId()][r.getId()][t.getId()]);
                    }
                }
                IntVar[] arr = temp.toArray(new IntVar[0]);
                model.addLinearConstraint(LinearExpr.sum(arr), 0, 1); // <=1 -> max one out of overlap is allowed
            }
        }
        // END CONSTRAINT

        // START CONSTRAINT 1. Coaches should have as little free timeslots between presentations as possible.
        IntVar[][] lecturerTimeslot = new IntVar[lecturers.size()][timeslots.size()]; // Coach has a presentation at timeslot
        int[] timeslotCost = new int[timeslots.size()];

        IntVar[] firstTimeslots = new IntVar[lecturers.size()];
        IntVar[] diffs = new IntVar[lecturers.size()];
        IntVar[] lastTimeslots = new IntVar[lecturers.size()];

        for (Lecturer l : lecturers) {
            for (Timeslot t : timeslots) {
                lecturerTimeslot[l.getId()][t.getId()] = model.newBoolVar("lecturerTimeslot_" + l.getId() + "_" + t.getId());

                timeslotCost[t.getId()] = t.getPriority();
                ArrayList<IntVar> temp = new ArrayList<>();

                for (Room r : rooms) {
                    for (Presentation p : presentations) {
                        if (!(p.getExpert().getId() == l.getId() || p.getCoach().getId() == l.getId())) {
                            continue;
                        } // If lecturer is not coach or expert for this presentation, skip the presentation
                        if (presRoomTime[p.getId()][r.getId()][t.getId()] == null) continue;
                        temp.add(presRoomTime[p.getId()][r.getId()][t.getId()]);
                    }
                }
                IntVar[] arr = temp.toArray(new IntVar[0]);
                // Implement lecturerTimeslot[c][t] == (sum(arr) >= 1)
                model.addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(lecturerTimeslot[l.getId()][t.getId()]);
                model.addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(lecturerTimeslot[l.getId()][t.getId()].not());
            }
        }
        for (Lecturer l : lecturers) { // Calculate first / last timeslot and difference per lecturer
            firstTimeslots[l.getId()] = model.newIntVar(0, timeslots.size(), "firstTimeslot" + l.getId());
            lastTimeslots[l.getId()] = model.newIntVar(0, timeslots.size(), "lastTimeslot" + l.getId());
            diffs[l.getId()] = model.newIntVar(0, timeslots.size(), "diff_" + l.getId());

            for (Timeslot t : timeslots) {
                model.addGreaterOrEqual(lastTimeslots[l.getId()], t.getId()).onlyEnforceIf(lecturerTimeslot[l.getId()][t.getId()]);
                model.addLessOrEqual(firstTimeslots[l.getId()], t.getId()).onlyEnforceIf(lecturerTimeslot[l.getId()][t.getId()]);
            }
            LinearExpr diffExpr = LinearExpr.scalProd(new IntVar[]{lastTimeslots[l.getId()], firstTimeslots[l.getId()]}, new int[]{1, -1}); // Last timeslot - first timeslot
            model.addEquality(diffExpr, diffs[l.getId()]);
            objIntVars.add(diffs[l.getId()]); // add it to the objective
            objIntCoeffs.add(LECTURER_PER_LESSON_COST);
        }

        // END CONSTRAINT

        // START CONSTRAINT Soft Constraint 1.1 Coaches should switch the rooms as little as possible


        // Variable / Array setup for all the things
        IntVar[][][] coachTimeRoomBool = new IntVar[lecturers.size()][timeslots.size()][rooms.size()];
        IntVar[][] coachRoomTime = new IntVar[lecturers.size()][timeslots.size()];
        IntVar[][] roomDiffsInt = new IntVar[lecturers.size()][timeslots.size()];
        IntVar[][] roomDiffsBool = new IntVar[lecturers.size()][timeslots.size()];
        IntVar[] numChangesForLecturer = new IntVar[lecturers.size()];
        for (Lecturer l : lecturers) {
            for (Timeslot t : timeslots) {
                coachRoomTime[l.getId()][t.getId()] = model.newIntVar(-1, rooms.size(), "coach_" + l.getId() + "time_" + t.getId()); // Number of room lecturer has at room/time
                roomDiffsInt[l.getId()][t.getId()] = model.newIntVar(0, 100000000L, "coach_" + l.getId() + "time_" + t.getId()); // Room ID difference between presentations
                roomDiffsBool[l.getId()][t.getId()] = model.newBoolVar("coach_" + l.getId() + "switchAt_time_" + t.getId()); // TRUE if coach switches rooms at time, FALSE if not
                for (Room r : rooms) {
                    coachTimeRoomBool[l.getId()][t.getId()][r.getId()] = model.newBoolVar("coach_" + l.getId() + "time_" + t.getId() + "room_" + r.getId()); //Boolean if leturer has pres at room in time
                }
            }
            numChangesForLecturer[l.getId()] = model.newIntVar(0, timeslots.size(), "numRoomChangesForLecturer" + l.getId()); //Number of changes for lecturer
        }
        for (Lecturer l : lecturers) {
            for (Timeslot t : timeslots) {
                for (Room r : rooms) {
                    List<IntVar> temp = new ArrayList<>();
                    for (Presentation p1 : presentationsPerLecturer[l.getId()]) {
                        if (presRoomTime[p1.getId()][r.getId()][t.getId()] == null) continue;
                        temp.add(presRoomTime[p1.getId()][r.getId()][t.getId()]);
                    }
                    IntVar[] arr = temp.toArray(new IntVar[0]);
                    // If a presentation is happening in room at time, true, else false.
                       //model.addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(coachTimeRoomBool[l.getId()][t.getId()][r.getId()]);
                       //model.addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(coachTimeRoomBool[l.getId()][t.getId()][r.getId()].not());
                    model.addEquality(LinearExpr.sum(arr), coachTimeRoomBool[l.getId()][t.getId()][r.getId()]); // same as above??

                    //model.addEquality(coachTimeRoomBool[l.getId()][t.getId()][r.getId()],0).onlyEnforceIf(lecturerTimeslot[l.getId()][t.getId()].not());

                    // Integer conversion -> coach has Room at Time = number
                    model.addHint(coachRoomTime[l.getId()][t.getId()], -1);
                    model.addEquality(coachRoomTime[l.getId()][t.getId()], r.getId()).onlyEnforceIf(coachTimeRoomBool[l.getId()][t.getId()][r.getId()]); // set value to roomid if lecturer has pres at this time
                }
                /*
                IntVar hasPresAtCurrTime = model.newBoolVar("hasPres"+l.getId()+"AtTime"+t.getId());
                model.addGreaterOrEqual(LinearExpr.sum(coachTimeRoomBool[l.getId()][t.getId()]),1).onlyEnforceIf(hasPresAtCurrTime);
                model.addLessOrEqual(LinearExpr.sum(coachTimeRoomBool[l.getId()][t.getId()]),0).onlyEnforceIf(hasPresAtCurrTime.not());
                model.addEquality(coachRoomTime[l.getId()][t.getId()], -1).onlyEnforceIf(hasPresAtCurrTime.not());// set value -1 if lecturer has no presentation at this time
                   */


                //Set no room (-1) if the lecturer doesnt have a pres at this time
                model.addEquality(coachRoomTime[l.getId()][t.getId()], -1).onlyEnforceIf(lecturerTimeslot[l.getId()][t.getId()].not());




            }


            for (Timeslot t : timeslots) {
                if (t.getId() == 0) {
                    model.addEquality(roomDiffsInt[l.getId()][0], 0); // difference between the 0 index of array is 0 because there wasnt a presentation before
                    continue;
                } // skip the first presentation as there cant be a earlier presentation
                LinearExpr diffExpr = LinearExpr.scalProd(new IntVar[]{coachRoomTime[l.getId()][t.getId()], coachRoomTime[l.getId()][t.getId() - 1]}, new int[]{1, -1}); // current timeslot room ID - previous timeslot room ID

                //model.addAbsEquality(roomDiffsBool[l.getId()][t.getId()],1).onlyEnforceIf();
                // ^ coachRoomTime[l.getId()][t.getId() - 1]} is same as coachRoomTime[l.getId()][t.getId()]}

                IntVar absDiffInt = model.newIntVar(-100000000L,100000000L,"DiffInt_l"+l.getId()+"t_"+t.getId());
                  model.addEquality(diffExpr, absDiffInt);
                  model.addAbsEquality(roomDiffsInt[l.getId()][t.getId()],absDiffInt);

                // if difference is greaterEqual than 1, switch is true
                  model.addGreaterOrEqual(roomDiffsInt[l.getId()][t.getId()], 1).onlyEnforceIf(roomDiffsBool[l.getId()][t.getId()]);
                  model.addLessOrEqual(roomDiffsInt[l.getId()][t.getId()], 0).onlyEnforceIf(roomDiffsBool[l.getId()][t.getId()].not());
            }



            // Problem here somewhere
            numChangesForLecturer[l.getId()] = model.newIntVar(0, timeslots.size(), "numRoomChangesForLecturer" + l.getId()); //Number of changes for lecturer is sum of changed booleans
            model.addEquality(numChangesForLecturer[l.getId()], LinearExpr.sum(roomDiffsBool[l.getId()])); // Add the equality

            // finally, add the objective
            //objIntVars.add(numChangesForLecturer[l.getId()]);
            //objIntCoeffs.add(ROOM_SWITCH_COST);
        }


        // START CONSTRAINT Soft Constraint 1.2 Coaches should switch the rooms as little as possible
        // Create (lecturer,room) booleans, minimize
        /*
        IntVar[][] coachRoom = new IntVar[lecturers.size()][rooms.size()];
        int[] coachRoomCost = new int[rooms.size()];
        for (Lecturer l : lecturers) {
            for (Room r : rooms) {
                coachRoom[l.getId()][r.getId()] = model.newBoolVar("coach_" + l.getId() + "room_" + r.getId());
                coachRoomCost[r.getId()] = ROOM_SWITCH_COST;
            }
        }
        for (Lecturer l : lecturers) {
            for (Room r : rooms) {
                List<IntVar> temp = new ArrayList<>();

                for (Presentation p1 : presentationsPerLecturer[l.getId()]) {
                    for (Timeslot t : timeslots) {
                        if (presRoomTime[p1.getId()][r.getId()][t.getId()] == null) continue;
                        temp.add(presRoomTime[p1.getId()][r.getId()][t.getId()]);
                    }
                }
                IntVar[] arr = temp.toArray(new IntVar[0]);

                // Implement coachRoom[l][r] == (sum(arr) >= 1).
                model.addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(coachRoom[l.getId()][r.getId()]);
                model.addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(coachRoom[l.getId()][r.getId()].not());

                // Add to objective
                objIntVars.add(coachRoom[l.getId()][r.getId()]);
                objIntCoeffs.add(coachRoomCost[r.getId()]);

            }
        }
        */

        // END CONSTRAINT




        // START CONSTRAINT 3.1 As little rooms as possible should be free per timeslots -> Minimize used Timeslots
        IntVar[] timeslotUsed = new IntVar[timeslots.size()];
        for (Timeslot t : timeslots) {
            timeslotUsed[t.getId()] = model.newBoolVar("timeslotUsed_" + t.getId());
        }

        for (Timeslot t : timeslots) {
            List<IntVar> temp = new ArrayList<IntVar>();

            for (Room r : rooms) {
                for (Presentation p : presentations) {
                    if (presRoomTime[p.getId()][r.getId()][t.getId()] == null) continue;
                    temp.add(presRoomTime[p.getId()][r.getId()][t.getId()]);
                }
            }
            IntVar[] arr = temp.toArray(new IntVar[0]);
            /// IF SUM ARR > 0 add boolean timeslotUsed TRUE else FALSE;
            // Implement timeslotUsed[t] == (sum(arr) >= 1).
            model.addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(timeslotUsed[t.getId()]);
            model.addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(timeslotUsed[t.getId()].not());

            //Add Objective
            objIntVars.add(timeslotUsed[t.getId()]);
            objIntCoeffs.add(timeslotCost[t.getId()]);
        }
        // END CONSTRAINT

        // START CONSTRAINT 4 As little rooms as possible should be used over all -> Minimize used Rooms over all timeslots
        IntVar[] roomUsed = new IntVar[rooms.size()];
        int[] roomCost = new int[rooms.size()];
        for (Room r : rooms) {
            roomUsed[r.getId()] = model.newBoolVar("roomUsed_" + r.getId());
            roomCost[r.getId()] = USED_ROOM_COST;
        }

        for (Room r : rooms) {
            List<IntVar> temp = new ArrayList<IntVar>();

            for (Timeslot t : timeslots) {
                for (Presentation p : presentations) {
                    if (presRoomTime[p.getId()][r.getId()][t.getId()] == null) continue;
                    temp.add(presRoomTime[p.getId()][r.getId()][t.getId()]);
                }
            }
            IntVar[] arr = temp.toArray(new IntVar[0]);
            /// IF SUM ARR > 0 add boolean roomUsed TRUE else FALSE;
            // Implement timeslotUsed[t] == (sum(arr) >= 1).
            model.addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(roomUsed[r.getId()]);
            model.addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(roomUsed[r.getId()].not());

            //Add Objective
            objIntVars.add(roomUsed[r.getId()]);
            objIntCoeffs.add(roomCost[r.getId()]);
        }
        // END CONSTRAINT


        // Add the objective to the Solver, parse to array first because java is funny like that
        int[] objIntCoeffsArr = objIntCoeffs.stream().mapToInt(i -> i).toArray();
        IntVar[] objIntVarsArr = objIntVars.toArray(new IntVar[0]);

        // finally, minimize the objective
        model.minimize(LinearExpr.scalProd(objIntVarsArr, objIntCoeffsArr));

        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(timelimit);
        System.out.println("All constraints done, solving");
        System.out.println(model.validate());
        PresentationSolutionObserver cb = new PresentationSolutionObserver(presRoomTime, lecturers, presentations, timeslots, rooms, stopWatch, solverContext, firstTimeslots, lecturerTimeslot, lastTimeslots, diffs, coachRoomTime, roomDiffsInt, roomDiffsBool, numChangesForLecturer, coachTimeRoomBool);

        CpSolverStatus res = solver.searchAllSolutions(model, cb);
        System.out.println(res);
        solverContext.setSolving(false);
        stopWatch.stop();
        Planning p = solverContext.getPlanning();
        p.setStatus(res.name());
        return p;
    }
}
