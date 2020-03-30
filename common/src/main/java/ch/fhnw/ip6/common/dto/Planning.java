package ch.fhnw.ip6.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Planning {

    private int nr;
    private int cost;
    private Set<Solution> solutions = new HashSet<>();

    @Override
    public String toString() {
        List<Timeslot> timeslots = solutions.stream().map(Solution::getTimeSlot).sorted(TimeslotComparator::compareAnInt).distinct().collect(Collectors.toList());
        List<Room> rooms = solutions.stream().map(Solution::getRoom).sorted(RoomComparator::compareAnInt).distinct().collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        sb.append("Planning Nr. ").append(nr).append(" with cost ").append(cost).append(System.lineSeparator());
        sb.append("         |");

        rooms.forEach(r -> sb.append(r.getName()).append("|"));
        sb.append(System.lineSeparator());

        timeslots.forEach(t -> {
            sb.append(t.getDate()).append(" |");
            rooms.forEach(r ->
                    solutions
                            .stream()
                            .filter(s -> s.getRoom().equals(r) && s.getTimeSlot().equals(t))
                            .findFirst()
                            .ifPresentOrElse(
                                    s -> sb.append(String.format("%03d", s.getPresentation().getId())).append("|")
                                    , () -> sb.append("   |")));
            sb.append(System.lineSeparator());
        });


        return sb.toString();
    }

    static class TimeslotComparator implements Comparator<Timeslot> {

        public static int compareAnInt(Timeslot t1, Timeslot t2) {
            return t1.getId() - t2.getId();
        }

        @Override
        public int compare(Timeslot t1, Timeslot t2) {
            return compareAnInt(t1, t2);
        }
    }


    static class RoomComparator implements Comparator<Room> {

        public static int compareAnInt(Room r1, Room r2) {
            return r1.getId() - r2.getId();
        }

        @Override
        public int compare(Room r1, Room r2) {
            return compareAnInt(r1, r2);
        }
    }
}
