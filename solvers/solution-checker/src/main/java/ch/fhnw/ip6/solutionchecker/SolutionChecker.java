package ch.fhnw.ip6.solutionchecker;

import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.common.dto.RoomDto;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.TimeslotDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static ch.fhnw.ip6.common.util.CostUtil.*;

public class SolutionChecker {


    public static int getSolutionCost(Set<Solution> solutions, List<LecturerDto> lecturers, List<PresentationDto> presentations, List<TimeslotDto> timeslots, List<RoomDto> rooms) {
        CheckOnePresentationPerTimeslotForProfessor(solutions, presentations, timeslots, lecturers);
        CheckEachPresentationOnce(solutions, presentations);
        int roomDoubleBookedCost = CheckRoomUsedMaxOncePerTime(solutions, rooms, timeslots) * ROOM_DOUBLE_BOOKED_COST;
        int roomSwitchCost = GetRoomSwitches(solutions, lecturers, timeslots) * ROOM_SWITCH_COST;
        int usedRoomsCost = GetUsedRooms(solutions, rooms) * USED_ROOM_COST;
        int usedTimeslotCost = GetUsedTimeslots(solutions, timeslots);

        // total cost return
        return roomSwitchCost + roomDoubleBookedCost + usedRoomsCost + usedTimeslotCost;
    }

    private static int GetUsedTimeslots(Set<Solution> solutions, List<TimeslotDto> timeslots) {
        int[] presentationsPerTimeslot = new int[timeslots.size()];
        for (Solution s : solutions) {
            presentationsPerTimeslot[s.getTimeSlot().getId()]++;
        }
        int timeslotsUsed = 0;
        int timeslotsCost = 0;
        for (int i = 0; i < presentationsPerTimeslot.length; i++) {
            if (presentationsPerTimeslot[i] > 0) { // Timeslot has at least 1 presentation
                timeslotsUsed++;
                timeslotsCost += timeslots.get(i).getPriority();
            }
        }
        //return timeslotsUsed;
        return timeslotsCost;
    }

    private static int GetUsedRooms(Set<Solution> solutions, List<RoomDto> rooms) {
        int[] presentationsPerRoom = new int[rooms.size()];
        for (Solution s : solutions) {
            presentationsPerRoom[s.getTimeSlot().getId()]++;
        }
        int roomsUsed = 0;
        for (int t : presentationsPerRoom) {
            if (t > 0) { // Timeslot has at least 1 presentation
                roomsUsed++;
            }
        }
        return roomsUsed;
    }

    /*
     *
     *  */
    private static int GetRoomSwitches(Set<Solution> solutions, List<LecturerDto> lecturers, List<TimeslotDto> timeslots) {
        List<RoomDto>[] roomsPerLecturer = new List[lecturers.size()];
        // Initialize ArrayLists
        for (LecturerDto l : lecturers) {
            roomsPerLecturer[l.getId()] = new ArrayList<>();
        }


        for (TimeslotDto t : timeslots) {
            // Get solutions for current timeslot
            List<Solution> solForTime = solutions.stream().filter(s -> s.getTimeSlot().getId() == t.getId()).collect(Collectors.toList());
            for (Solution s : solForTime) {
                // If the last room for the lecturer is different than the room of the solution, add it. Else, don't, as the lecturer didnt switch rooms
                Optional<RoomDto> lastExpertRoom = Optional.empty();
                if (roomsPerLecturer[s.getExpert().getId()] != null) {
                    lastExpertRoom = roomsPerLecturer[s.getExpert().getId()].stream().reduce((first, second) -> second); // Get last element in array (sequential)
                }

                Optional<RoomDto> lastCoachRoom = Optional.empty();
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
        for (LecturerDto l : lecturers) {
            System.out.print("L: " + l.getId() + " :");
            for (RoomDto rs : roomsPerLecturer[l.getId()]) {
                System.out.print(rs.getId() + "->");
            }
            int switches = (roomsPerLecturer[l.getId()].size() - 1);
            if (switches > 0) {
                System.out.println("Total: " + switches + " switches.");
                totalSwitches += switches;
            } else {
                System.out.println("Total: no switches.");
            }
        }
        System.out.println("Total Switches over all Lecturers: " + totalSwitches + " !!!!!! This number is wrong, we need to fix the checker: Peer, 23.04.2020");
        return totalSwitches;
    }

    private static Boolean CheckOnePresentationPerTimeslotForProfessor(Set<Solution> results, List<PresentationDto> presentations, List<TimeslotDto> timeslots, List<LecturerDto> lecturers) {

        int[][] profTimeslot = new int[lecturers.size()][timeslots.size()];
        for (Solution r : results) {
            profTimeslot[r.getCoach().getId()][r.getTimeSlot().getId()]++;
            profTimeslot[r.getExpert().getId()][r.getTimeSlot().getId()]++;
        }

        int numErrors = 0;
        for (int i = 0; i < profTimeslot.length; i++) {
            for (int j = 0; j < profTimeslot[i].length; j++) {
                if (profTimeslot[i][j] > 1) {

                    int finalJ = j;
                    System.out.println("   Error: Professor " + i + " has " + profTimeslot[i][j] + " presentations at time " + timeslots.stream().filter(t -> t.getId() == finalJ).findFirst().get().getDate());
                    numErrors++;
                }
            }
        }
        System.out.println("Total Double Booked Presentations/Professors: " + numErrors);
        return numErrors > 0;
    }

    private static Boolean CheckEachPresentationOnce(Set<Solution> results, List<PresentationDto> presentations) {
        int[] presentationsScheduledTime = new int[presentations.size()];
        for (Solution result : results) {
            presentationsScheduledTime[result.getPresentation().getId()]++;
        }

        for (int i = 0; i < presentationsScheduledTime.length; i++) {
            if (presentationsScheduledTime[i] != 1) {
                System.out.println("PresentationNotOnceError: Presentation " + i + " is scheduled " + presentationsScheduledTime[i] + " times.");
                return false;
            }
        }
        System.out.println("Each Presentation occures once");
        return true;
    }

    private static int CheckRoomUsedMaxOncePerTime(Set<Solution> results, List<RoomDto> rooms,
                                                   List<TimeslotDto> timeslots) {
        int[][] roomPerTime = new int[timeslots.size()][rooms.size()];
        int doubleBookings = 0;
        for (Solution r : results) {
            if (roomPerTime[r.getTimeSlot().getId()][r.getRoom().getId()] == 0) {
                roomPerTime[r.getTimeSlot().getId()][r.getRoom().getId()] = r.getPresentation().getId();
            } else {
                System.out.println("RoomDoubleUseError: Room " + r.getRoom().getId() + " at time " + r.getTimeSlot().getDate() + " is already in use for presentation " + roomPerTime[r.getTimeSlot().getId()][r.getRoom().getId()] + ". Can't add Presentation " + r.getPresentation().getId() + " at the same time! ");
                doubleBookings++;
            }

        }
        System.out.println("Double room Bookings: " + doubleBookings);
        return doubleBookings;
    }

}
