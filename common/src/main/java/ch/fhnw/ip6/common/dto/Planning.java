package ch.fhnw.ip6.common.dto;

import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Planning {

    private int nr;
    private int cost;
    private Set<Solution> solutions = new HashSet<>();
    private List<? extends T> timeslots = new ArrayList<>();
    private List<? extends R> rooms = new ArrayList<>();
    private String status;

    private String planningStats;

    public String getPlanningAsTable() {


        // If we only want to show rooms that have presentations at all, use the following lines
        // List<Room> rooms = solutions.stream().map(Solution::getRoom).sorted(RoomComparator::compareAnInt).distinct().collect(Collectors.toList());


        AsciiTable planning = new AsciiTable();
        List<String> roomHeaders = rooms.stream().map(R::getName).collect(Collectors.toList());
        roomHeaders.add(0, "");
        planning.addRule();
        planning.addRow(roomHeaders.toArray());
        planning.addRule();
        timeslots.forEach(t -> {
            List<String> roomCells = new ArrayList<>();
            roomCells.add(t.getDate());
            rooms.forEach(r -> {
                Optional<Solution> o = solutions
                        .stream()
                        .filter(s -> s.getRoom().equals(r) && s.getTimeSlot().equals(t))
                        .findFirst();
                if (o.isPresent()) {
                    roomCells.add("P: " + o.get().getPresentation().getNr() + "<br>"
                            + "C: " + o.get().getCoach().getInitials() + "<br>"
                            + "E: " + o.get().getExpert().getInitials());
                } else {
                    roomCells.add("");
                }

            });
            planning.addRow(roomCells.toArray());
            planning.addRule();
        });
        planning.setTextAlignment(TextAlignment.LEFT);
        planning.getRenderer().setCWC(new CWC_LongestLine());
        return planning.render();
    }

    public void setPlanningStats(String planningStats) {
        this.planningStats = planningStats;
    }

    public String getPlanningStats(){
        return planningStats;
    }
}
