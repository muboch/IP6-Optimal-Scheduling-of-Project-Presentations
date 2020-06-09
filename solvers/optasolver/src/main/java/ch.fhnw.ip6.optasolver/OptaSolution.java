package ch.fhnw.ip6.optasolver;


import java.util.List;


import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.common.dto.RoomDto;
import ch.fhnw.ip6.common.dto.TimeslotDto;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
@PlanningSolution
public class OptaSolution {

    @ProblemFactCollectionProperty
    private List<LecturerDto> lecturersList;
    @ValueRangeProvider(id = "timeslotRange")
    @ProblemFactCollectionProperty
    private List<TimeslotDto> timeslotList;

    @ValueRangeProvider(id = "roomRange")
    @ProblemFactCollectionProperty
    private List<RoomDto> roomList;

    @PlanningEntityCollectionProperty
    private List<PresentationDto> presentationList;



    @PlanningScore
    private HardSoftScore score;
    private OptaSolution() {}


    public OptaSolution(List<TimeslotDto> timeslots, List<RoomDto> rooms, List<PresentationDto> presentations, List<LecturerDto> lecturers) {
        this.timeslotList = timeslots;
        this.roomList = rooms;
        this.presentationList = presentations;
        this.lecturersList = lecturers;
    }


    // ********************************
    // Getters and setters
    // ********************************

    public List<TimeslotDto> getTimeslotList() {
        return timeslotList;
    }

    public List<RoomDto> getRoomList() {
        return roomList;
    }

    public List<PresentationDto> getPresentationList() {
        return presentationList;
    }

    public HardSoftScore getScore() {
        return score;
    }

}