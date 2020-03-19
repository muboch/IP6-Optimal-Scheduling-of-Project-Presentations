package ch.fhnw.ip6.solutionchecker;

import ch.fhnw.ip6.common.classes.*;

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
}
