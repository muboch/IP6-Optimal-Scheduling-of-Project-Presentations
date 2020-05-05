package ch.fhnw.ip6.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Planning {

    private int nr;
    private int cost;
    private Set<Solution> solutions = new HashSet<>();
    private List<TimeslotDto> timeslots = new ArrayList<>();
    private List<RoomDto> rooms = new ArrayList<>();
    private String status;

    @Override
    public String toString() {
        // If we only want to show rooms that have presentations at all, use the following lines
        // List<Room> rooms = solutions.stream().map(Solution::getRoom).sorted(RoomComparator::compareAnInt).distinct().collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        sb.append("Planning Nr. ").append(nr).append(" with cost ").append(cost).append(System.lineSeparator());
        sb.append("         |");

        rooms.forEach(r -> sb.append(r.getName()).append("|"));
        sb.append(System.lineSeparator());

        timeslots.forEach(t -> {
            sb.append(t.getDate()).append(" |");
            rooms.forEach(r -> {
                Optional<Solution> o = solutions
                        .stream()
                        .filter(s -> s.getRoom().equals(r) && s.getTimeSlot().equals(t))
                        .findFirst();
                if (o.isPresent())
                    sb.append(String.format("%03d", o.get().getPresentation().getId())).append("|");
                else
                    sb.append("   |");
            });

            sb.append(System.lineSeparator());
        });


        return sb.toString();
    }

    static class TimeslotComparator implements Comparator<TimeslotDto> {

        public static int compareAnInt(TimeslotDto t1, TimeslotDto t2) {
            return t1.getId() - t2.getId();
        }

        @Override
        public int compare(TimeslotDto t1, TimeslotDto t2) {
            return compareAnInt(t1, t2);
        }
    }


    static class RoomComparator implements Comparator<RoomDto> {

        public static int compareAnInt(RoomDto r1, RoomDto r2) {
            return r1.getId() - r2.getId();
        }

        @Override
        public int compare(RoomDto r1, RoomDto r2) {
            return compareAnInt(r1, r2);
        }
    }
}
