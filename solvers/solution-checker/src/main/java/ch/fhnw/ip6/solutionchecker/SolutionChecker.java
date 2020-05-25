package ch.fhnw.ip6.solutionchecker;

import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ch.fhnw.ip6.common.util.CostUtil.*;

public class SolutionChecker {

    private Set<String> errorsEachPresentationOnce = new HashSet<>();
    private Set<String> errorsOnePresentationPerTimeslotForLecturer = new HashSet<>();
    private Set<String> errorsCheckRoomUsedMaxOncePerTime = new HashSet<>();
    private Set<String> errorsRoomSwitches = new HashSet<>();

    private int totalPlanningCost;
    private int totalRoomSwitchesCosts;
    private int totalUsedTimeslotsCosts;
    private int totalUsedRoomsCosts;

    public void generateStats(Planning planning, List<L> lecturers, List<P> presentations, List<T> timeslots, List<R> rooms) {
        Set<Solution> solutions = planning.getSolutions();

        boolean checkOnePresentationPerTimeslotForLecturer = checkOnePresentationPerTimeslotForLecturer(solutions, timeslots, lecturers);
        boolean checkEachPresentationOnce = checkEachPresentationOnce(solutions, presentations);
        boolean checkRoomUsedMaxOncePerTime = checkRoomUsedMaxOncePerTime(solutions, rooms, timeslots);
        boolean checkRoomSwitches = getRoomSwitches(solutions, lecturers, timeslots);
        boolean checkUsedRooms = getUsedRooms(solutions, rooms, presentations, timeslots);
        boolean checkUsedTimeslots = getUsedTimeslots(solutions, timeslots, presentations, rooms);

        int roomDoubleBookedCost = getTotalDoubleBookedCosts();
        int roomSwitchCost = getTotalRoomSwitchesCosts();
        int usedRoomsCost = getTotalUsedRoomsCosts();
        int usedTimeslotCost = getTotalUsedTimeslotsCosts();

        setTotalPlanningCost(roomSwitchCost
                + roomDoubleBookedCost
                + usedRoomsCost
                + usedTimeslotCost);

        // Global Stats
        AsciiTable stats = new AsciiTable();
        stats.setTextAlignment(TextAlignment.LEFT);
        stats.getRenderer().setCWC(new CWC_LongestLine());
        stats.addRule();
        stats.addRow(null, "Planning Stats");
        stats.addRule();
        stats.addRow("Cost:", getTotalPlanningCost());
        stats.addRow("Room double booked Costs:", roomDoubleBookedCost);
        stats.addRow("Room Switch Costs:", roomSwitchCost);
        stats.addRow("Used Room Costs:", usedRoomsCost);
        stats.addRow("Used Timeslot Cost:", usedTimeslotCost);
        stats.addRule();

        // Hard Constraint Stats
        AsciiTable hardConstraints = new AsciiTable();
        hardConstraints.addRule();
        hardConstraints.addRow(null, null, "Hard Constraint Validation Results");
        hardConstraints.addRule();
        hardConstraints.addRow("Check", "Status", "Error");
        hardConstraints.addRule();
        hardConstraints.addRow("double booked Rooms:", isPassedOrFailed(checkOnePresentationPerTimeslotForLecturer), printErrors(errorsOnePresentationPerTimeslotForLecturer));
        hardConstraints.addRule();
        hardConstraints.addRow("each Presentation only one time scheduled:", isPassedOrFailed(checkEachPresentationOnce), printErrors(errorsEachPresentationOnce));
        hardConstraints.addRule();
        hardConstraints.addRow("each Room only used once per Time:", isPassedOrFailed(checkRoomUsedMaxOncePerTime), printErrors(errorsCheckRoomUsedMaxOncePerTime));
        hardConstraints.addRule();

        // Soft Constraint Stats
        AsciiTable softConstraints = new AsciiTable();
        softConstraints.addRule();
        softConstraints.addRow(null, null, "Soft Constraint Validation Results");
        softConstraints.addRule();
        softConstraints.addRow("Check", "Status", "Error");
        softConstraints.addRule();
        softConstraints.addRow("Room Switches:", isPassedOrFailed(checkRoomSwitches), printErrors(errorsRoomSwitches));
        softConstraints.addRule();
        softConstraints.addRow("Rooms Used:", isPassedOrFailed(checkUsedRooms), getTotalUsedRoomsCosts());
        softConstraints.addRule();
        softConstraints.addRow("Timeslots Used:", isPassedOrFailed(checkUsedTimeslots), getTotalUsedTimeslotsCosts());
        softConstraints.addRule();
        softConstraints.setTextAlignment(TextAlignment.LEFT);
        softConstraints.getRenderer().setCWC(new CWC_LongestLine());

        String planningStats = stats.render();
        planningStats += "\n";
        planningStats += hardConstraints.render();
        planningStats += "\n";
        planningStats += softConstraints.render();
        planning.setPlanningStats(planningStats);

    }

    private int getTotalDoubleBookedCosts() {
        return errorsCheckRoomUsedMaxOncePerTime.size() * ROOM_DOUBLE_BOOKED_COST;
    }


    private StringBuilder printErrors(Set<String> errorsCheckRoomUsedMaxOncePerTime) {
        StringBuilder sbCheckRoomUsedMaxOncePerTime = new StringBuilder();
        errorsCheckRoomUsedMaxOncePerTime.forEach(s -> sbCheckRoomUsedMaxOncePerTime.append("* ").append(s).append("<br>"));
        return sbCheckRoomUsedMaxOncePerTime;
    }


    private boolean getUsedTimeslots(Set<Solution> solutions, List<T> timeslots, List<P> presentations, List<R> rooms) {
        int[] presentationsPerTimeslot = new int[timeslots.size()];
        for (Solution s : solutions) {
            presentationsPerTimeslot[timeslots.indexOf(s.getTimeSlot())]++;
        }
        int timeslotsCost = 0;
        int timeslotsUsed = 0;
        for (int i = 0; i < presentationsPerTimeslot.length; i++) {
            if (presentationsPerTimeslot[i] > 0) { // Timeslot has at least 1 presentation
                timeslotsUsed++;
                timeslotsCost += timeslots.get(i).getPriority();
            }
        }
        setTotalUsedTimeslotCost(timeslotsCost);
        return timeslotsUsed <= presentations.size() / rooms.size();
    }


    private boolean getUsedRooms(Set<Solution> solutions, List<R> rooms, List<P> presentations, List<T> timeslots) {
        int[] presentationsPerRoom = new int[rooms.size()];
        for (Solution s : solutions) {
            presentationsPerRoom[rooms.indexOf(s.getRoom())]++;
        }
        int roomsUsed = 0;
        for (int t : presentationsPerRoom) {
            if (t > 0) {
                roomsUsed++;
            }
        }
        setTotalUsedRoomsCosts(roomsUsed * USED_ROOM_COST);
        return roomsUsed <= presentations.size() / timeslots.size();
    }

    /*
     * Counts the number of room switches per lecturer.
     *
     */
    private boolean getRoomSwitches(Set<Solution> solutions, List<L> lecturers, List<T> timeslots) {
        List<R>[] roomsPerLecturer = new List[lecturers.size()];

        // Initialize ArrayLists
        for (L l : lecturers) {
            roomsPerLecturer[lecturers.indexOf(l)] = new ArrayList<>();
        }


        for (T t : timeslots) {
            // Get solutions for current timeslot
            List<Solution> solForTime = solutions.stream().filter(s -> s.getTimeSlot().getId() == t.getId()).collect(Collectors.toList());
            for (Solution s : solForTime) {
                // If the last room for the lecturer is different than the room of the solution, add it. Else, don't, as the lecturer didnt switch rooms
                Optional<R> lastExpertRoom = Optional.empty();
                if (roomsPerLecturer[lecturers.indexOf(s.getExpert())] != null) {
                    lastExpertRoom = roomsPerLecturer[lecturers.indexOf(s.getExpert())].stream().reduce((first, second) -> second); // Get last element in array (sequential)
                }

                Optional<R> lastCoachRoom = Optional.empty();
                if (roomsPerLecturer[lecturers.indexOf(s.getCoach())] != null) {
                    lastCoachRoom = roomsPerLecturer[lecturers.indexOf(s.getCoach())].stream().reduce((first, second) -> second); // Get last element in array (sequential)
                }


                if (!lastExpertRoom.isPresent() || lastExpertRoom.get().getId() != s.getRoom().getId()) {
                    roomsPerLecturer[lecturers.indexOf(s.getExpert())].add(s.getRoom());
                }

                if (!lastCoachRoom.isPresent() || lastCoachRoom.get().getId() != s.getRoom().getId()) {
                    roomsPerLecturer[lecturers.indexOf(s.getCoach())].add(s.getRoom());
                }

            }
        }

        int totalSwitches = 0;

        for (L l : lecturers) {
            if (roomsPerLecturer[lecturers.indexOf(l)].size() > 1) {
                AtomicInteger roomSwitches = new AtomicInteger();
                StringBuilder roomSwitchesSB = new StringBuilder();

                roomSwitchesSB.append(roomsPerLecturer[lecturers.indexOf(l)].get(0).getName());
                roomsPerLecturer[lecturers.indexOf(l)].stream().reduce((r1, r2) -> {
                    if (r1 != r2) {
                        roomSwitches.getAndIncrement();
                        roomSwitchesSB.append("->").append(r2.getName());
                        return r2;
                    }
                    return r1;
                });
                errorsRoomSwitches.add(l.getInitials() + " [" + roomSwitches + "] " + roomSwitchesSB + "<br>");
                totalSwitches += roomSwitches.get();
            }
        }

        setTotalRoomSwitchesCosts(totalSwitches * ROOM_SWITCH_COST);
        return totalSwitches <= lecturers.size();
    }


    /**
     * Checks if each Lecturer has only one Presentation at time.
     *
     * @param results
     * @param timeslots
     * @param lecturers
     * @return
     */
    private boolean checkOnePresentationPerTimeslotForLecturer(Set<Solution> results, List<T> timeslots, List<L> lecturers) {

        int[][] profTimeslot = new int[lecturers.size()][timeslots.size()];
        for (Solution r : results) {
            profTimeslot[lecturers.indexOf(r.getCoach())][timeslots.indexOf(r.getTimeSlot())]++;
            profTimeslot[lecturers.indexOf(r.getExpert())][timeslots.indexOf(r.getTimeSlot())]++;
        }

        for (int i = 0; i < profTimeslot.length; i++) {
            for (int j = 0; j < profTimeslot[i].length; j++) {
                if (profTimeslot[i][j] > 1) {
                    errorsOnePresentationPerTimeslotForLecturer.add("Lec " + lecturers.get(i).getInitials() + " has " + profTimeslot[i][j] + " presentations at time " + timeslots.get(i).getDate());
                    // System.out.println("   Error: Professor " + lecturers.get(i) + " has " + profTimeslot[i][j] + " presentations at time " + timeslots.stream().filter(t -> timeslots.indexOf(t) == finalJ).findFirst().get().getDate());
                }
            }
        }
        return errorsOnePresentationPerTimeslotForLecturer.isEmpty();
    }

    /**
     * Check if each Presenation is scheduled only once.
     *
     * @param solutions
     * @param presentations
     * @return
     */
    private boolean checkEachPresentationOnce(Set<Solution> solutions, List<P> presentations) {

        int[] presentationsScheduledTime = new int[presentations.size()];

        for (Solution result : solutions) {
            presentationsScheduledTime[presentations.indexOf(result.getPresentation())]++;
        }

        for (int i = 0; i < presentationsScheduledTime.length; i++) {
            if (presentationsScheduledTime[i] != 1) {
                errorsEachPresentationOnce.add("Presentation " + i + " is scheduled " + presentationsScheduledTime[i] + " times.");
            }
        }
        return errorsEachPresentationOnce.isEmpty();
    }


    /**
     * Checks if a rooms is only used once per time.
     *
     * @param results
     * @param rooms
     * @param timeslots
     * @return
     */
    private boolean checkRoomUsedMaxOncePerTime(Set<Solution> results, List<R> rooms, List<T> timeslots) {
        int[][] roomPerTime = new int[timeslots.size()][rooms.size()];
        for (Solution r : results) {
            if (roomPerTime[timeslots.indexOf(r.getTimeSlot())][rooms.indexOf(r.getRoom())] == 0) {
                roomPerTime[timeslots.indexOf(r.getTimeSlot())][rooms.indexOf(r.getRoom())] = r.getPresentation().getId();
            } else {
                errorsCheckRoomUsedMaxOncePerTime.add("Room: " + r.getRoom().getName() + ", Time: " + r.getTimeSlot().getDate() + " Presenatation: " + roomPerTime[timeslots.indexOf(r.getTimeSlot())][rooms.indexOf(r.getRoom())]);
            }

        }
        return errorsCheckRoomUsedMaxOncePerTime.isEmpty();
    }

    public int getTotalPlanningCost() {
        return totalPlanningCost;
    }

    private void setTotalPlanningCost(int totalPlanningCost) {
        this.totalPlanningCost = totalPlanningCost;
    }

    private void setTotalRoomSwitchesCosts(int totalRoomSwitchesCosts) {
        this.totalRoomSwitchesCosts = totalRoomSwitchesCosts;
    }

    private int getTotalRoomSwitchesCosts() {
        return totalRoomSwitchesCosts;
    }

    private void setTotalUsedTimeslotCost(int totalUsedTimeslotsCosts) {
        this.totalUsedTimeslotsCosts = totalUsedTimeslotsCosts;
    }

    private String isPassedOrFailed(boolean checkRoomUsedMaxOncePerTime) {
        return checkRoomUsedMaxOncePerTime ? "Passed" : "Failed";
    }


    public void setTotalUsedRoomsCosts(int totalUsedRoomsCosts) {
        this.totalUsedRoomsCosts = totalUsedRoomsCosts;
    }

    private int getTotalUsedRoomsCosts() {
        return totalUsedRoomsCosts;
    }

    private int getTotalUsedTimeslotsCosts() {
        return totalUsedTimeslotsCosts;
    }

}
