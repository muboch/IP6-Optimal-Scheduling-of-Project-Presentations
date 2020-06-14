package ch.fhnw.ip6.common.dto;

import ch.fhnw.ip6.common.FreeTimeslotsUpdatingVarListener;
import ch.fhnw.ip6.common.dto.marker.L;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
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
    private Integer freeTimeslots;
    private List<PresentationDto> presentations;

    @CustomShadowVariable(variableListenerClass = FreeTimeslotsUpdatingVarListener.class,
            sources = {@PlanningVariableReference(variableName = "timeslot", entityClass = PresentationDto.class)})
    public Integer getFreeTimeslots() {
        return freeTimeslots;
    }

    public String getName(){
        return lastname + " " + firstname;
    }

    @Override
    public String toString() {
        return String.format("L[id=%03d,ini=%s]",id, initials);
    }

}
