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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
        int roomSwitches = getRoomSwitches(solutions, lecturers, timeslots, presentations);
        int usedRooms = getUsedRooms(solutions, rooms, presentations, timeslots);
        int usedTimeslots = getUsedTimeslots(solutions, timeslots, presentations, rooms);

        int roomDoubleBookedCost = getTotalDoubleBookedCosts();
        int roomSwitchCost = getTotalRoomSwitchesCosts();
        int usedRoomsCost = getTotalUsedRoomsCosts();
        int usedTimeslotCost = getTotalUsedTimeslotsCosts();

        setTotalPlanningCost(roomSwitchCost
                + roomDoubleBookedCost
                + usedRoomsCost
                + usedTimeslotCost);

        Map<L, Set<P>> presPerLecturer = new HashMap<>();

        presentations.forEach(p -> {
            presPerLecturer.computeIfAbsent(p.getExpert(), k -> new HashSet<>()).add(p);
            presPerLecturer.computeIfAbsent(p.getCoach(), k -> new HashSet<>()).add(p);
        });

        Map<Integer, Set<L>> numOfPres = presPerLecturer.entrySet().stream()
                .collect(Collectors.groupingBy(
                        e -> e.getValue().size(),
                        Collectors.mapping(Map.Entry::getKey, Collectors.toSet())
                ));

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
        stats.addRow("Presentations per Lecturer:",
                numOfPres.entrySet().stream().map(e -> e.getValue().size() + " L with " + e.getKey() + " P<br>").collect(Collectors.joining()));
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
        hardConstraints.setTextAlignment(TextAlignment.LEFT);
        hardConstraints.getRenderer().setCWC(new CWC_LongestLine());

        // Soft Constraint Stats
        AsciiTable softConstraints = new AsciiTable();
        softConstraints.addRule();
        softConstraints.addRow(null, "Soft Constraint Validation Results");
        softConstraints.addRule();
        softConstraints.addRow("Check", "Error");
        softConstraints.addRule();
        softConstraints.addRow("Room Switches:", "Total: " + roomSwitches + "<br>" + printErrors(errorsRoomSwitches));
        softConstraints.addRule();
        softConstraints.addRow("Rooms Used:", usedRooms);
        softConstraints.addRule();
        softConstraints.addRow("Timeslots Used:", usedTimeslots);
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


    int getUsedTimeslots(Set<Solution> solutions, List<T> timeslots, List<P> presentations, List<R> rooms) {
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
        return timeslotsUsed;
    }


    int getUsedRooms(Set<Solution> solutions, List<R> rooms, List<P> presentations, List<T> timeslots) {
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
        return roomsUsed;
    }

    /*
     * Counts the number of room switches per lecturer.
     *
     */
    int getRoomSwitches(Set<Solution> solutions, List<L> lecturers, List<T> timeslots, List<P> presentations) {
        errorsRoomSwitches = new HashSet<>();

        List<R>[] roomsPerLecturer = new List[lecturers.size()];

        Map<L, List<P>> presPerLec = new HashMap<>();
        for (P p : presentations) {
            if (presPerLec.get(p.getCoach()) == null) {
                List<P> pres = new ArrayList<>();
                pres.add(p);
                presPerLec.put(p.getCoach(), pres);
            } else {
                presPerLec.get(p.getCoach()).add(p);
            }
            if (presPerLec.get(p.getExpert()) == null) {
                List<P> pres = new ArrayList<>();
                pres.add(p);
                presPerLec.put(p.getExpert(), pres);
            } else {
                presPerLec.get(p.getExpert()).add(p);
            }
        }

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
                errorsRoomSwitches.add(l.getInitials() + " [" + roomSwitches + "|" + (presPerLec.get(l).size()) + "] " + roomSwitchesSB + "<br>");
                totalSwitches += roomSwitches.get();
            }
        }

        setTotalRoomSwitchesCosts(totalSwitches * ROOM_SWITCH_COST);
        return totalSwitches;
    }


    /**
     * Checks if each Lecturer has only one Presentation at time.
     *
     * @param results
     * @param timeslots
     * @param lecturers
     * @return
     */
    boolean checkOnePresentationPerTimeslotForLecturer(Set<Solution> results, List<T> timeslots, List<L> lecturers) {

        errorsOnePresentationPerTimeslotForLecturer = new HashSet<>();

        int[][] profTimeslot = new int[lecturers.size()][timeslots.size()];
        for (Solution r : results) {
            profTimeslot[lecturers.indexOf(r.getCoach())][timeslots.indexOf(r.getTimeSlot())]++;
            profTimeslot[lecturers.indexOf(r.getExpert())][timeslots.indexOf(r.getTimeSlot())]++;
        }

        for (int i = 0; i < profTimeslot.length; i++) {
            for (int j = 0; j < profTimeslot[i].length; j++) {
                if (profTimeslot[i][j] > 1) {
                    System.out.println("i:"+i+" j:"+j);
                    errorsOnePresentationPerTimeslotForLecturer.add("Lec " + lecturers.get(i).getInitials() + " has " + profTimeslot[i][j] + " presentations at time " + timeslots.get(j).getDate());
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
    boolean checkEachPresentationOnce(Set<Solution> solutions, List<P> presentations) {

        errorsEachPresentationOnce = new HashSet<>();

        int[] presentationsScheduledTime = new int[presentations.size()];

        for (Solution result : solutions) {
            presentationsScheduledTime[presentations.indexOf(result.getPresentation())]++;
        }

        for (int i = 0; i < presentationsScheduledTime.length; i++) {
            if (presentationsScheduledTime[i] != 1) {
                errorsEachPresentationOnce.add("Presentation " + presentations.get(i).getNr() + " is scheduled " + presentationsScheduledTime[i] + " times.");
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
    boolean checkRoomUsedMaxOncePerTime(Set<Solution> results, List<R> rooms, List<T> timeslots) {

        errorsCheckRoomUsedMaxOncePerTime = new HashSet<>();

        int[][] roomPerTime = new int[timeslots.size()][rooms.size()];
        for (Solution r : results) {
            if (roomPerTime[timeslots.indexOf(r.getTimeSlot())][rooms.indexOf(r.getRoom())] == 0) {
                roomPerTime[timeslots.indexOf(r.getTimeSlot())][rooms.indexOf(r.getRoom())] += 1;
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

    int getTotalRoomSwitchesCosts() {
        return totalRoomSwitchesCosts;
    }

    private void setTotalUsedTimeslotCost(int totalUsedTimeslotsCosts) {
        this.totalUsedTimeslotsCosts = totalUsedTimeslotsCosts;
    }

    private void setTotalUsedRoomsCosts(int totalUsedRoomsCosts) {
        this.totalUsedRoomsCosts = totalUsedRoomsCosts;
    }

    int getTotalUsedRoomsCosts() {
        return totalUsedRoomsCosts;
    }

    int getTotalUsedTimeslotsCosts() {
        return totalUsedTimeslotsCosts;
    }

    private String isPassedOrFailed(boolean checkRoomUsedMaxOncePerTime) {
        return checkRoomUsedMaxOncePerTime ? "Passed" : "Failed";
    }
}
