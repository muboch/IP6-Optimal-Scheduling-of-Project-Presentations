package ch.fhnw.ip6.common.dto;

import ch.fhnw.ip6.common.dto.marker.L;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PlanningEntity
public class LecturerDto implements L {

    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private String initials;

    @PlanningVariableReference(entityClass = PresentationDto.class, variableName = "presentations")
    private List<PresentationDto> presentations;

    @PlanningVariableReference(entityClass = RoomDto.class, variableName = "rooms")
    private List<RoomDto> rooms;

    @PlanningVariableReference(entityClass = TimeslotDto.class, variableName = "timeslots")
    private List<TimeslotDto> timeslots;

    public String getName(){
        return lastname + " " + firstname;
    }

    @Override
    public String toString() {
        return String.format("L[id=%03d,ini=%s]",id, initials);
    }
}
