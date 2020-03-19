package ch.fhnw.ip6.solutionchecker;

import ch.fhnw.ip6.common.classes.*;

import java.sql.Time;
import java.util.List;

public class SolutionChecker {
    public static void printSolution(List<Solution> Solutions, List<Lecturer> lecturers, List<Presentation> presentations, List<Timeslot> timeslots, List<Room> rooms, int solutionCount) {
        System.out.println("Solution " + solutionCount);//: time =+ " {WallTime():.02} s");
        System.out.print("         |");
        rooms.forEach(r -> System.out.print(r.getName()));
        System.out.println();
        for (var time : timeslots) {
            System.out.print(time.getDatum() + " |");
            for (var room : rooms) {
                var sol = Solutions.stream().filter(s -> s.getRoom() == room && s.getTimeSlot() == time).findFirst().get();
                if (sol != null) //
                {
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
                    System.out.println("   Error: Professor " + i + " has " + profTimeslot[i][j] + " presentations at time " + timeslots.stream().filter(t -> t.getId() == finalJ).findFirst().get().getDatum());
                    numErrors++;
                }
            }
        }
        System.out.println(" Total Double Booked Presentations: " + numErrors);
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
                System.out.println("RoomDoubleUseError: Room " + r.getRoom().getId() + " at time " + r.getTimeSlot().getDatum() + " is already in use for presentation " + roomPerTime[r.getTimeSlot().getId()][r.getRoom().getId()] + ". Can't add Presentation " + r.getPresentation().getId() + " at the same time! ");
                return false;
            }

        }
        System.out.println("No rooms used double");
        return true;
    }

}
