package ch.fhnw.ip6.optasolver;


import java.util.List;


import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
@PlanningSolution
public class OptaSolution {


    @ValueRangeProvider(id = "timeslotRange")
    @ProblemFactCollectionProperty
    private List<T> timeslotList;

    @ValueRangeProvider(id = "roomRange")
    @ProblemFactCollectionProperty
    private List<R> roomList;

    @PlanningEntityCollectionProperty
    private List<P> presentationList;

    @PlanningScore
    private HardSoftScore score;
    private OptaSolution() {}


    public OptaSolution(List<T> timeslots, List<R> rooms, List<P> presentations) {
        this.timeslotList = timeslots;
        this.roomList = rooms;
        this.presentationList = presentations;
    }


    // ********************************
    // Getters and setters
    // ********************************

    public List<T> getTimeslotList() {
        return timeslotList;
    }

    public List<R> getRoomList() {
        return roomList;
    }

    public List<P> getPresentationList() {
        return presentationList;
    }

    public HardSoftScore getScore() {
        return score;
    }

}