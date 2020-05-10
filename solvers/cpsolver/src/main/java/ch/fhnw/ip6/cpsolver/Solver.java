package ch.fhnw.ip6.cpsolver;

import ch.fhnw.ip6.api.AbstractSolver;
import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.RoomDto;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.common.dto.TimeslotDto;
import ch.fhnw.ip6.common.util.JsonUtil;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ch.fhnw.ip6.common.util.CostUtil.LECTURER_PER_LESSON_COST;
import static ch.fhnw.ip6.common.util.CostUtil.USED_ROOM_COST;

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

        List<PresentationDto> presentations = util.getJsonAsList("presentations.json", PresentationDto.class);
        List<LecturerDto> lecturers = util.getJsonAsList("lecturers.json", LecturerDto.class);
        List<RoomDto> rooms = util.getJsonAsList("rooms.json", RoomDto.class).stream().filter(r -> r.getReserve().equals(false)).collect(Collectors.toList());
        List<TimeslotDto> timeslots = util.getJsonAsList("timeslots.json", TimeslotDto.class);

        for (PresentationDto p : presentations) {
            p.setCoach(lecturers.stream().filter(t -> t.getInitials().equals(p.getCoachInitials())).findFirst().get()); // Assign Coaches to Presentation
            p.setExpert(lecturers.stream().filter(t -> t.getInitials().equals(p.getExpertInitials())).findFirst().get()); // Assign Experts to Presentation
        }

        return solve(presentations, lecturers, rooms, timeslots, new boolean[lecturers.size()][timeslots.size()]);
    }

    @Override
    public Planning solve(List<PresentationDto> presentations, List<LecturerDto> lecturers, List<RoomDto> rooms, List<TimeslotDto> timeslots, boolean[][] locktimes) {
        solverContext.setSolving(true);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        presentations.forEach(System.out::println);
        rooms.forEach(System.out::println);
        timeslots.forEach(System.out::println);
        lecturers.forEach(System.out::println);

        CPModel cpModel = new CPModel(presentations, lecturers, rooms, timeslots, locktimes, new CpModel());

        //Create cpModel.getModel() presTimeRoom[p,t,r] == 1 -> Presentation p happens in room r at time t
        IntVar[][][] presRoomTime = cpModel.getX();

        System.out.println("Setup completed");
        // For each lecturer, list the presentations that are not allowed to overlap
        List<PresentationDto>[] presentationsPerLecturer = new ArrayList[lecturers.size()];
        for (L l : lecturers) {
            presentationsPerLecturer[cpModel.indexOf(l)] = presentations.stream().filter(ps -> ps.getExpert().getId() == l.getId() || ps.getCoach().getId() == l.getId()).collect(Collectors.toList());
        }
        System.out.println("Overlap calculation completed");

        // Data structures for Objectives
        ArrayList<IntVar> objIntVars = new ArrayList<>();
        ArrayList<Integer> objIntCoeffs = new ArrayList<>();


        // START CONSTRAINT:  For each Presentation, there must be 1 (room,timeslot) pair. -> Each presentation must be presented in a room at a time
        for (P p : presentations) {
            List<IntVar> temp = new ArrayList<>();
            for (T t : timeslots) {
                for (R r : rooms) {
                    if (presRoomTime[cpModel.indexOf(p)][cpModel.indexOf(r)][cpModel.indexOf(t)] == null) continue;

                    temp.add(presRoomTime[cpModel.indexOf(p)][cpModel.indexOf(r)][cpModel.indexOf(t)]);
                }
            }
            IntVar[] arr = temp.toArray(new IntVar[0]);
            cpModel.getModel().addLinearConstraint(LinearExpr.sum(arr), 1, 1); // SUM OF ALL MUST EQUAL ONE
        }
        // END CONSTRAINT

        // START CONSTRAINT For each (room, timeslot) pair there must be <=1 presentation -> Max 1 Presentation per Room/Time
        for (R r : rooms) {
            for (T t : timeslots) {
                List<IntVar> temp = new ArrayList<>();
                for (PresentationDto p : presentations) {
                    if (presRoomTime[cpModel.indexOf(p)][cpModel.indexOf(r)][cpModel.indexOf(t)] == null) continue;
                    temp.add(presRoomTime[cpModel.indexOf(p)][cpModel.indexOf(r)][cpModel.indexOf(t)]);
                }
                IntVar[] arr = temp.toArray(new IntVar[0]);
                cpModel.getModel().addLinearConstraint(LinearExpr.sum(arr), 0, 1);
            }
        }
        // END CONSTRAINT


        // START CONSTRAINT Foreach presentation, the following conflicting (presentation,room, time) pairs are not allowed -> Lecturers may not have more than one presentation at a time.
        for (LecturerDto l : lecturers) {
            for (T t : timeslots) {
                List<IntVar> temp = new ArrayList<>();
                for (R r : rooms) {
                    for (PresentationDto p1 : presentationsPerLecturer[cpModel.indexOf(l)]) {
                        if (presRoomTime[p1.getId()][cpModel.indexOf(r)][cpModel.indexOf(t)] == null) continue;
                        temp.add(presRoomTime[p1.getId()][cpModel.indexOf(r)][cpModel.indexOf(t)]);
                    }
                }
                IntVar[] arr = temp.toArray(new IntVar[0]);
                cpModel.getModel().addLinearConstraint(LinearExpr.sum(arr), 0, 1); // <=1 -> max one out of overlap is allowed
            }
        }
        // END CONSTRAINT

        // START CONSTRAINT 1. Coaches should have as little free timeslots between presentations as possible.
        IntVar[][] lecturerTimeslot = new IntVar[lecturers.size()][timeslots.size()]; // Coach has a presentation at timeslot
        int[] timeslotCost = new int[timeslots.size()];

        IntVar[] firstTimeslots = new IntVar[lecturers.size()];
        IntVar[] diffs = new IntVar[lecturers.size()];
        IntVar[] lastTimeslots = new IntVar[lecturers.size()];

        for (LecturerDto l : lecturers) {
            for (T t : timeslots) {
                lecturerTimeslot[cpModel.indexOf(l)][cpModel.indexOf(t)] = cpModel.getModel().newBoolVar("lecturerTimeslot_" + cpModel.indexOf(l) + "_" + t.getId());

                timeslotCost[cpModel.indexOf(t)] = t.getPriority();
                ArrayList<IntVar> temp = new ArrayList<>();

                for (R r : rooms) {
                    for (PresentationDto p : presentations) {
                        if (!(p.getExpert().getId() == cpModel.indexOf(l) || p.getCoach().getId() == l.getId())) {
                            continue;
                        } // If lecturer is not coach or expert for this presentation, skip the presentation
                        if (presRoomTime[cpModel.indexOf(p)][cpModel.indexOf(r)][cpModel.indexOf(t)] == null) continue;
                        temp.add(presRoomTime[cpModel.indexOf(p)][cpModel.indexOf(r)][cpModel.indexOf(t)]);
                    }
                }
                IntVar[] arr = temp.toArray(new IntVar[0]);
                // Implement lecturerTimeslot[c][t] == (sum(arr) >= 1)
                cpModel.getModel().addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(lecturerTimeslot[cpModel.indexOf(l)][cpModel.indexOf(t)]);
                cpModel.getModel().addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(lecturerTimeslot[cpModel.indexOf(l)][cpModel.indexOf(t)].not());
            }
        }
        for (LecturerDto l : lecturers) { // Calculate first / last timeslot and difference per lecturer
            firstTimeslots[cpModel.indexOf(l)] = cpModel.getModel().newIntVar(0, timeslots.size(), "firstTimeslot" + l.getId());
            lastTimeslots[cpModel.indexOf(l)] = cpModel.getModel().newIntVar(0, timeslots.size(), "lastTimeslot" + l.getId());
            diffs[cpModel.indexOf(l)] = cpModel.getModel().newIntVar(0, timeslots.size(), "diff_" + l.getId());

            for (T t : timeslots) {
                cpModel.getModel().addGreaterOrEqual(lastTimeslots[cpModel.indexOf(l)], t.getId()).onlyEnforceIf(lecturerTimeslot[cpModel.indexOf(l)][cpModel.indexOf(t)]);
                cpModel.getModel().addLessOrEqual(firstTimeslots[cpModel.indexOf(l)], t.getId()).onlyEnforceIf(lecturerTimeslot[cpModel.indexOf(l)][cpModel.indexOf(t)]);
            }
            LinearExpr diffExpr = LinearExpr.scalProd(new IntVar[]{lastTimeslots[cpModel.indexOf(l)], firstTimeslots[cpModel.indexOf(l)]}, new int[]{1, -1}); // Last timeslot - first timeslot
            cpModel.getModel().addEquality(diffExpr, diffs[cpModel.indexOf(l)]);
            objIntVars.add(diffs[cpModel.indexOf(l)]); // add it to the objective
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
        for (LecturerDto l : lecturers) {
            for (T t : timeslots) {
                coachRoomTime[cpModel.indexOf(l)][cpModel.indexOf(t)] = cpModel.getModel().newIntVar(-1, rooms.size(), "coach_" + cpModel.indexOf(l) + "time_" + t.getId()); // Number of room lecturer has at room/time
                roomDiffsInt[cpModel.indexOf(l)][cpModel.indexOf(t)] = cpModel.getModel().newIntVar(0, 100000000L, "coach_" + cpModel.indexOf(l) + "time_" + t.getId()); // Room ID difference between presentations
                roomDiffsBool[cpModel.indexOf(l)][cpModel.indexOf(t)] = cpModel.getModel().newBoolVar("coach_" + cpModel.indexOf(l) + "switchAt_time_" + t.getId()); // TRUE if coach switches rooms at time, FALSE if not
                for (R r : rooms) {
                    coachTimeRoomBool[cpModel.indexOf(l)][cpModel.indexOf(t)][cpModel.indexOf(r)] = cpModel.getModel().newBoolVar("coach_" + cpModel.indexOf(l) + "time_" + cpModel.indexOf(t) + "room_" + r.getId()); //Boolean if leturer has pres at room in time
                }
            }
            numChangesForLecturer[cpModel.indexOf(l)] = cpModel.getModel().newIntVar(0, timeslots.size(), "numRoomChangesForLecturer" + l.getId()); //Number of changes for lecturer
        }
        for (LecturerDto l : lecturers) {
            for (T t : timeslots) {
                for (R r : rooms) {
                    List<IntVar> temp = new ArrayList<>();
                    for (PresentationDto p1 : presentationsPerLecturer[cpModel.indexOf(l)]) {
                        if (presRoomTime[p1.getId()][cpModel.indexOf(r)][cpModel.indexOf(t)] == null) continue;
                        temp.add(presRoomTime[p1.getId()][cpModel.indexOf(r)][cpModel.indexOf(t)]);
                    }
                    IntVar[] arr = temp.toArray(new IntVar[0]);
                    // If a presentation is happening in room at time, true, else false.
                    //model.addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(coachTimeRoomBool[l.getId()][t.getId()][r.getId()]);
                    //model.addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(coachTimeRoomBool[l.getId()][t.getId()][r.getId()].not());
                    //model.addEquality(coachTimeRoomBool[l.getId()][t.getId()][r.getId()],0).onlyEnforceIf(lecturerTimeslot[l.getId()][t.getId()].not());
                    cpModel.getModel().addEquality(LinearExpr.sum(arr), coachTimeRoomBool[cpModel.indexOf(l)][cpModel.indexOf(t)][cpModel.indexOf(r)]); // same as above??

                    // Integer conversion -> coach has Room at Time = number
                    cpModel.getModel().addHint(coachRoomTime[cpModel.indexOf(l)][cpModel.indexOf(t)], -1);
                    cpModel.getModel().addEquality(coachRoomTime[cpModel.indexOf(l)][cpModel.indexOf(t)], r.getId()).onlyEnforceIf(coachTimeRoomBool[cpModel.indexOf(l)][cpModel.indexOf(t)][cpModel.indexOf(r)]); // set value to roomid if lecturer has pres at this time
                }
                /*
                IntVar hasPresAtCurrTime = model.newBoolVar("hasPres"+l.getId()+"AtTime"+t.getId());
                model.addGreaterOrEqual(LinearExpr.sum(coachTimeRoomBool[l.getId()][t.getId()]),1).onlyEnforceIf(hasPresAtCurrTime);
                model.addLessOrEqual(LinearExpr.sum(coachTimeRoomBool[l.getId()][t.getId()]),0).onlyEnforceIf(hasPresAtCurrTime.not());
                model.addEquality(coachRoomTime[l.getId()][t.getId()], -1).onlyEnforceIf(hasPresAtCurrTime.not());// set value -1 if lecturer has no presentation at this time
                   */


                //Set no room (-1) if the lecturer doesnt have a pres at this time
                cpModel.getModel().addEquality(coachRoomTime[cpModel.indexOf(l)][cpModel.indexOf(t)], -1).onlyEnforceIf(lecturerTimeslot[cpModel.indexOf(l)][cpModel.indexOf(t)].not());


            }


            for (T t : timeslots) {
                if (cpModel.indexOf(t) == 0) {
                    cpModel.getModel().addEquality(roomDiffsInt[cpModel.indexOf(l)][0], 0); // difference between the 0 index of array is 0 because there wasnt a presentation before
                    continue;
                }
                // skip the first presentation as there cant be a earlier presentation
                LinearExpr diffExpr = LinearExpr.scalProd(new IntVar[]{coachRoomTime[cpModel.indexOf(l)][cpModel.indexOf(t)], coachRoomTime[cpModel.indexOf(l)][cpModel.indexOf(t) - 1]}, new int[]{1, -1}); // current timeslot room ID - previous timeslot room ID


                IntVar absDiffInt = cpModel.getModel().newIntVar(-100000000L, 100000000L, "DiffInt_l" + cpModel.indexOf(l) + "t_" + t.getId());
                cpModel.getModel().addEquality(diffExpr, absDiffInt);
                cpModel.getModel().addAbsEquality(roomDiffsInt[cpModel.indexOf(l)][cpModel.indexOf(t)], absDiffInt);

                // if difference is greaterEqual than 1, switch is true
                cpModel.getModel().addGreaterOrEqual(roomDiffsInt[cpModel.indexOf(l)][cpModel.indexOf(t)], 1).onlyEnforceIf(roomDiffsBool[cpModel.indexOf(l)][cpModel.indexOf(t)]);
                cpModel.getModel().addLessOrEqual(roomDiffsInt[cpModel.indexOf(l)][cpModel.indexOf(t)], 0).onlyEnforceIf(roomDiffsBool[cpModel.indexOf(l)][cpModel.indexOf(t)].not());
            }


            // Problem here somewhere
            numChangesForLecturer[cpModel.indexOf(l)] = cpModel.getModel().newIntVar(0, timeslots.size(), "numRoomChangesForLecturer" + l.getId()); //Number of changes for lecturer is sum of changed booleans
            cpModel.getModel().addEquality(numChangesForLecturer[cpModel.indexOf(l)], LinearExpr.sum(roomDiffsBool[cpModel.indexOf(l)])); // Add the equality

            // finally, add the objective
            //objIntVars.add(numChangesForLecturer[l.getId()]);
            //objIntCoeffs.add(ROOM_SWITCH_COST);
        }

        // END CONSTRAINT


        // START CONSTRAINT 3.1 As little rooms as possible should be free per timeslots -> Minimize used Timeslots
        IntVar[] timeslotUsed = new IntVar[timeslots.size()];
        for (T t : timeslots) {
            timeslotUsed[cpModel.indexOf(t)] = cpModel.getModel().newBoolVar("timeslotUsed_" + t.getId());
        }

        for (T t : timeslots) {
            List<IntVar> temp = new ArrayList<IntVar>();

            for (R r : rooms) {
                for (PresentationDto p : presentations) {
                    if (presRoomTime[cpModel.indexOf(p)][cpModel.indexOf(r)][cpModel.indexOf(t)] == null) continue;
                    temp.add(presRoomTime[cpModel.indexOf(p)][cpModel.indexOf(r)][cpModel.indexOf(t)]);
                }
            }
            IntVar[] arr = temp.toArray(new IntVar[0]);
            /// IF SUM ARR > 0 add boolean timeslotUsed TRUE else FALSE;
            // Implement timeslotUsed[t] == (sum(arr) >= 1).
            cpModel.getModel().addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(timeslotUsed[cpModel.indexOf(t)]);
            cpModel.getModel().addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(timeslotUsed[cpModel.indexOf(t)].not());

            //Add Objective
            objIntVars.add(timeslotUsed[cpModel.indexOf(t)]);
            objIntCoeffs.add(timeslotCost[cpModel.indexOf(t)]);
        }
        // END CONSTRAINT

        // START CONSTRAINT 4 As little rooms as possible should be used over all -> Minimize used Rooms over all timeslots
        IntVar[] roomUsed = new IntVar[rooms.size()];
        int[] roomCost = new int[rooms.size()];
        for (R r : rooms) {
            roomUsed[cpModel.indexOf(r)] = cpModel.getModel().newBoolVar("roomUsed_" + r.getId());
            roomCost[cpModel.indexOf(r)] = USED_ROOM_COST;
        }

        for (R r : rooms) {
            List<IntVar> temp = new ArrayList<IntVar>();

            for (T t : timeslots) {
                for (PresentationDto p : presentations) {
                    if (presRoomTime[cpModel.indexOf(p)][cpModel.indexOf(r)][cpModel.indexOf(t)] == null) continue;
                    temp.add(presRoomTime[cpModel.indexOf(p)][cpModel.indexOf(r)][cpModel.indexOf(t)]);
                }
            }
            IntVar[] arr = temp.toArray(new IntVar[0]);
            /// IF SUM ARR > 0 add boolean roomUsed TRUE else FALSE;
            // Implement timeslotUsed[t] == (sum(arr) >= 1).
            // If A then B
            cpModel.getModel().addGreaterOrEqual(LinearExpr.sum(arr), 1).onlyEnforceIf(roomUsed[cpModel.indexOf(r)]);
            cpModel.getModel().addLessOrEqual(LinearExpr.sum(arr), 0).onlyEnforceIf(roomUsed[cpModel.indexOf(r)].not());

            //Add Objective
            objIntVars.add(roomUsed[cpModel.indexOf(r)]);
            objIntCoeffs.add(roomCost[cpModel.indexOf(r)]);
        }
        // END CONSTRAINT


        // Add the objective to the Solver, parse to array first because java is funny like that
        int[] objIntCoeffsArr = objIntCoeffs.stream().mapToInt(i -> i).toArray();
        IntVar[] objIntVarsArr = objIntVars.toArray(new IntVar[0]);

        // finally, minimize the objective
        cpModel.getModel().minimize(LinearExpr.scalProd(objIntVarsArr, objIntCoeffsArr));

        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(timelimit);
        System.out.println("All constraints done, solving");
        System.out.println(cpModel.getModel().validate());
        PresentationSolutionObserver cb = new PresentationSolutionObserver(presRoomTime, lecturers, presentations, timeslots, rooms, stopWatch, solverContext, firstTimeslots, lecturerTimeslot, lastTimeslots, diffs, coachRoomTime, roomDiffsInt, roomDiffsBool, numChangesForLecturer, coachTimeRoomBool);

        CpSolverStatus res = solver.searchAllSolutions(cpModel.getModel(), cb);
        System.out.println(res);
        solverContext.setSolving(false);
        stopWatch.stop();
        Planning p = solverContext.getPlanning();
        p.setStatus(res.name());
        return p;
    }
}
