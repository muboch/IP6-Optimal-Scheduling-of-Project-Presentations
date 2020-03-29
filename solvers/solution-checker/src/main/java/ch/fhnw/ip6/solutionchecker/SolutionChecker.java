package ch.fhnw.ip6.solutionchecker;

import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.Timeslot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SolutionChecker {
    public static void printSolution(List<Solution> Solutions, List<Lecturer> lecturers, List<Presentation> presentations, List<Timeslot> timeslots, List<Room> rooms, int solutionCount) {
        System.out.println("Solution " + solutionCount);//: time =+ " {WallTime():.02} s");
        System.out.print("         |");
        rooms.forEach(r -> System.out.print(r.getName()+"|"));
        System.out.println();
        for (var time : timeslots) {
            System.out.print(time.getDate() + " |");
            for (var room : rooms) {
                var solop = Solutions.stream().filter(s -> s.getRoom() == room && s.getTimeSlot() == time).findFirst();
                if (solop.isPresent()) //
                {
                    var sol = solop.get();
                    System.out.print(String.format("%03d", sol.getPresentation().getId()) + "|");
                } else {
                    System.out.print("   |");
                }
            }
            System.out.println();
        }
    }

    public static void checkSolutionForCorrectness(List<Solution> Solutions, List<Lecturer> lecturers, List<Presentation> presentations, List<Timeslot> timeslots, List<Room> rooms) {
        CheckOnePresentationPerTimeslotForProfessor(Solutions, presentations, timeslots, lecturers);
        CheckEachPresentationOnce(Solutions, presentations);
        CheckRoomUsedMaxOncePerTime(Solutions,rooms,timeslots);
        CheckRoomSwitchesPerLecturer(Solutions,lecturers, timeslots);
    }

    private static void CheckRoomSwitchesPerLecturer(List<Solution> solutions, List<Lecturer> lecturers, List<Timeslot> timeslots) {
        List<Room>[] roomsPerLecturer = new List[lecturers.size()];
        // Initialize ArrayLists
        for(var l: lecturers){
            roomsPerLecturer[l.getId()] = new ArrayList<>();
        }


        for (var t: timeslots){
            // Get solutions for current timeslot
            var solForTime = solutions.stream().filter(s -> s.getTimeSlot().getId() == t.getId()).collect(Collectors.toList());
            for(var s: solForTime){
                // If the last room for the lecturer is different than the room of the solution, add it. Else, don't, as the lecturer didnt switch rooms
                Optional<Room> lastExpertRoom = Optional.empty();
                if( roomsPerLecturer[s.getExpert().getId()] != null){
                    lastExpertRoom = roomsPerLecturer[s.getExpert().getId()].stream().reduce((first, second) -> second); // Get last element in array (sequential)
                }

                Optional<Room> lastCoachRoom = Optional.empty();
                if( roomsPerLecturer[s.getCoach().getId()] != null){
                    lastCoachRoom = roomsPerLecturer[s.getCoach().getId()].stream().reduce((first, second) -> second); // Get last element in array (sequential)
                }


                if(!lastExpertRoom.isPresent() ||  lastExpertRoom.get().getId() != s.getRoom().getId()){
                    roomsPerLecturer[s.getExpert().getId()].add(s.getRoom());
                }

                if(!lastCoachRoom.isPresent() ||  lastCoachRoom.get().getId() != s.getRoom().getId()){
                    roomsPerLecturer[s.getCoach().getId()].add(s.getRoom());
                }

            }
        }

        int totalSwitches = 0;
        System.out.println("RoomSwitches Per Lecturer:");
        for (var l: lecturers){
            System.out.print("L: "+ l.getId()+ " :");
            for (var rs: roomsPerLecturer[l.getId()]){
                System.out.print(rs.getId()+"->");
            }
            var switches = (roomsPerLecturer[l.getId()].size()-1);
            if (switches > 0){
                System.out.println("Total: " + switches + " switches.");
                totalSwitches += switches;
            } else {
                System.out.println("Total: no switches.");
            }
        }
        System.out.println("Total Switches over all Lecturers: "+ totalSwitches);



    }

    private static Boolean CheckOnePresentationPerTimeslotForProfessor(List<Solution> results, List<Presentation> presentations, List<Timeslot> timeslots, List<Lecturer> lecturers) {

        var profTimeslot = new int[lecturers.size()][timeslots.size()];
        for (var r : results) {
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
        System.out.println("Total Double Booked Presentations: " + numErrors);
        return numErrors > 0;
    }

    private static Boolean CheckEachPresentationOnce(List<Solution> results, List<Presentation> presentations) {
        var presentationsScheduledTime = new int[presentations.size()];
        for (var result : results) {
            presentationsScheduledTime[result.getPresentation().getId()]++;
        }

        for (int i = 0; i < presentationsScheduledTime.length; i++) {
            if (presentationsScheduledTime[i] != 1) {
                System.out.println("PresentationNotOnceError: Presentation " + i + " is scheduled " + presentationsScheduledTime[i] + " times.");
                return false;
            }
        }
        System.out.println("No Presentation Occures more than once");
        return true;
    }

    private static Boolean CheckRoomUsedMaxOncePerTime(List<Solution> results, List<Room> rooms,
                                                       List<Timeslot> timeslots) {
        var roomPerTime = new int[timeslots.size()][rooms.size()];
        for (var r : results) {
            if (roomPerTime[r.getTimeSlot().getId()][r.getRoom().getId()] == 0) {
                roomPerTime[r.getTimeSlot().getId()][r.getRoom().getId()] = r.getPresentation().getId();
            } else {
                System.out.println("RoomDoubleUseError: Room " + r.getRoom().getId() + " at time " + r.getTimeSlot().getDate() + " is already in use for presentation " + roomPerTime[r.getTimeSlot().getId()][r.getRoom().getId()] + ". Can't add Presentation " + r.getPresentation().getId() + " at the same time! ");
                return false;
            }

        }
        System.out.println("No rooms used double");
        return true;
    }

}
