package ch.fhnw.ip6.solutionchecker;

import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.Timeslot;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static ch.fhnw.ip6.common.util.CostUtil.*;

public class SolutionCheckerCarlo {

    private final List<Room> rooms;
    private final List<Timeslot> timeslots;
    private final List<Presentation> presentations;
    private final List<Lecturer> lecturers;
    private final Planning planning;

    private int roomSwitchCosts;
    private int roomDoubleBookedCosts;
    private int usedRoomsCosts;
    private int usedTimeslotCosts;

    public SolutionCheckerCarlo(Planning planning, List<Lecturer> lecturers, List<Presentation> presentations, List<Timeslot> timeslots, List<Room> rooms) {
        this.planning = planning;
        this.lecturers = lecturers;
        this.presentations = presentations;
        this.timeslots = timeslots;
        this.rooms = rooms;
    }

    public void check() {
        checkPassed(checkOnePresentationPerTimeslotForProfessor(), "Check Double Booked Presentaions/Professors: ");
        checkPassed(checkEachPresentationOnce(), "Check Each Presentation Once: ");
        checkPassed(checkRoomUsedMaxOncePerTime(), "Check Room Used Max Once Per Time: ");
        checkPassed(checkRoomSwitches(), "Check Room Switched: ");
        checkPassed(checkUsedTimeslots(), "Check Timeslots used: ");
        checkPassed(checkUsedRooms(), "Check Rooms used: ");
    }

    private void checkPassed(ImmutablePair<Boolean, Integer> result, String message) {
        String msgAddition = "";
        if (result.getLeft()) msgAddition += '\u2713';
        else msgAddition = result.getRight().toString();
        System.out.println(message + msgAddition);
    }

    public int getCost() {
        return getRoomSwitchCosts() + getRoomDoubleBookedCosts() + getUsedRoomsCosts() + getUsedTimeslotCosts();
    }

    private ImmutablePair<Boolean, Integer> checkUsedTimeslots() {
        int[] presentationsPerTimeslot = new int[timeslots.size()];
        for (Solution s : planning.getSolutions()) {
            presentationsPerTimeslot[s.getTimeSlot().getId()]++;
        }
        int timeslotsCost = 0;
        for (int i = 0; i < presentationsPerTimeslot.length; i++) {
            if (presentationsPerTimeslot[i] > 0) { // Timeslot has at least 1 presentation
                timeslotsCost += timeslots.get(i).getPriority();
            }
        }
        setUsedTimeslotCosts(timeslotsCost);
        return new ImmutablePair<>(timeslotsCost > ch.fhnw.ip6.common.util.CostUtil.MAX_TIMESLOTS_USED_COST, timeslotsCost);
    }

    private ImmutablePair<Boolean, Integer> checkUsedRooms() {
        int[] presentationsPerRoom = new int[rooms.size()];
        for (Solution s : planning.getSolutions()) {
            presentationsPerRoom[s.getTimeSlot().getId()]++;
        }
        int roomsUsed = 0;
        for (int t : presentationsPerRoom) {
            if (t > 0) { // Timeslot has at least 1 presentation
                roomsUsed++;
            }
        }
        setUsedRoomsCosts(roomsUsed);
        return new ImmutablePair<>(roomsUsed > ch.fhnw.ip6.common.util.CostUtil.MAX_ROOMS_USED_COST, roomsUsed);
    }

    private ImmutablePair<Boolean, Integer> checkRoomSwitches() {

        List<Room>[] roomsPerLecturer = new List[lecturers.size()];
        // Initialize ArrayLists
        for (Lecturer l : lecturers) {
            roomsPerLecturer[l.getId()] = new ArrayList<>();
        }


        for (Timeslot t : timeslots) {
            // Get solutions for current timeslot
            List<Solution> solForTime = planning.getSolutions().stream().filter(s -> s.getTimeSlot().getId() == t.getId()).collect(Collectors.toList());
            for (Solution s : solForTime) {
                // If the last room for the lecturer is different than the room of the solution, add it. Else, don't, as the lecturer didnt switch rooms
                Optional<Room> lastExpertRoom = Optional.empty();
                if (roomsPerLecturer[s.getExpert().getId()] != null) {
                    lastExpertRoom = roomsPerLecturer[s.getExpert().getId()].stream().reduce((first, second) -> second); // Get last element in array (sequential)
                }

                Optional<Room> lastCoachRoom = Optional.empty();
                if (roomsPerLecturer[s.getCoach().getId()] != null) {
                    lastCoachRoom = roomsPerLecturer[s.getCoach().getId()].stream().reduce((first, second) -> second); // Get last element in array (sequential)
                }


                if (!lastExpertRoom.isPresent() || lastExpertRoom.get().getId() != s.getRoom().getId()) {
                    roomsPerLecturer[s.getExpert().getId()].add(s.getRoom());
                }

                if (!lastCoachRoom.isPresent() || lastCoachRoom.get().getId() != s.getRoom().getId()) {
                    roomsPerLecturer[s.getCoach().getId()].add(s.getRoom());
                }

            }
        }

        int totalSwitches = 0;
        System.out.println("RoomSwitches Per Lecturer:");
        lecturers.stream().filter(l -> roomsPerLecturer[l.getId()].size() > 1).forEach(l -> {
            AtomicInteger roomSwitches = new AtomicInteger();
            System.out.print(l.getInitials() +": ");
            System.out.print(roomsPerLecturer[l.getId()].get(0).getName());
            roomsPerLecturer[l.getId()].stream().reduce((r1, r2) -> {
                if (r1 != r2) {
                    roomSwitches.getAndIncrement();
                    System.out.print("->" + r2.getName());
                    return r2;
                }
                return r1;
            });
            System.out.println("    Total Switches: " + roomSwitches.get());
        });

        return new ImmutablePair<>(getRoomSwitchCosts() > ch.fhnw.ip6.common.util.CostUtil.MAX_ROOM_SWITCHES_COST, getRoomSwitchCosts());
    }

    private ImmutablePair<Boolean, Integer> checkOnePresentationPerTimeslotForProfessor() {

        Map<Lecturer, List<Timeslot>> lecturerTimeslots = new HashMap<>();
        for (Solution s : planning.getSolutions()) {
            lecturerTimeslots.compute(s.getCoach(), (k, v) -> v == null ? new ArrayList<>() : v).add(s.getTimeSlot());
            lecturerTimeslots.compute(s.getExpert(), (k, v) -> v == null ? new ArrayList<>() : v).add(s.getTimeSlot());
        }

        AtomicInteger errors = new AtomicInteger();
        lecturerTimeslots.forEach((l, ts) -> ts.forEach(t -> {
                    int frequency = Collections.frequency(ts, t);
                    if (frequency > 1) {
                        errors.getAndIncrement();
                        System.out.println("   Error: Lecturer " + l + " has " + frequency + " presentations at time " + t.getDate());

                    }
                })
        );
        return new ImmutablePair<>(errors.get() == 0, errors.get());
    }

    private ImmutablePair<Boolean, Integer> checkEachPresentationOnce() {
        Map<Presentation, Integer> presentationsScheduledTime = new HashMap<>();
        for (Solution result : planning.getSolutions()) {
            presentationsScheduledTime.compute(result.getPresentation(), (key, val) -> val == null ? val = 1 : val++);
        }
        AtomicReference<Boolean> passed = new AtomicReference<>(true);
        AtomicInteger parallelPresentations = new AtomicInteger();
        presentationsScheduledTime.forEach((p, n) -> {
            if (n > 1) {
                System.out.println("PresentationNotOnceError: Presentation " + p + " is scheduled " + n + " times.");
                passed.set(false);
                parallelPresentations.getAndIncrement();
            }
        });
        return new ImmutablePair<>(passed.get(), parallelPresentations.get());
    }

    private ImmutablePair<Boolean, Integer> checkRoomUsedMaxOncePerTime() {
        Map<Room, List<Timeslot>> roomTimeslots = new HashMap<>();

        AtomicInteger doubleBookings = new AtomicInteger();

        planning.getSolutions().forEach(s -> roomTimeslots.compute(s.getRoom(), (k, v) -> v == null ? new ArrayList<>() : v).add(s.getTimeSlot()));

        roomTimeslots.forEach((r, timeslots) -> {
            timeslots.forEach(t -> {
                int frequency = Collections.frequency(timeslots, t);
                if (frequency > 1) {
                    doubleBookings.getAndIncrement();
                    System.out.println("   Error: Room " + r + " has " + frequency + " presentations at time " + t.getDate());
                }
            });
        });
        setRoomDoubleBookedCosts(doubleBookings.get());
        return new ImmutablePair<>(doubleBookings.get() == 0, doubleBookings.get());
    }


    private int getRoomSwitchCosts() {
        return roomSwitchCosts;
    }

    private void setRoomSwitchCosts(int roomSwitchCosts) {
        this.roomSwitchCosts = roomSwitchCosts * ROOM_SWITCH_COST;
    }

    private int getRoomDoubleBookedCosts() {
        return roomDoubleBookedCosts;
    }

    private void setRoomDoubleBookedCosts(int roomDoubleBookedCosts) {
        this.roomDoubleBookedCosts = roomDoubleBookedCosts * ROOM_DOUBLE_BOOKED_COST;
    }

    private int getUsedRoomsCosts() {
        return usedRoomsCosts;
    }

    private void setUsedRoomsCosts(int usedRoomsCosts) {
        this.usedRoomsCosts = usedRoomsCosts * USED_ROOM_COST;
    }

    private int getUsedTimeslotCosts() {
        return usedTimeslotCosts;
    }

    private void setUsedTimeslotCosts(int usedTimeslotCosts) {
        this.usedTimeslotCosts = usedTimeslotCosts * USED_TIMESLOT_COST;
    }

}
