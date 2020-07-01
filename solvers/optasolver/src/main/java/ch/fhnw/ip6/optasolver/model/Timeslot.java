package ch.fhnw.ip6.optasolver.model;

import ch.fhnw.ip6.common.dto.TimeslotDto;
import lombok.EqualsAndHashCode;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import java.util.ArrayList;
import java.util.List;

@PlanningEntity
@EqualsAndHashCode(callSuper = true, exclude = "presentations")
public class Timeslot extends TimeslotDto {

    private List<Presentation> presentations;

    @PlanningId
    public Long getPlanningId(){
        return (long) super.getId();
    }

    @InverseRelationShadowVariable(sourceVariableName = "timeslot")
    public List<Presentation> getPresentationList() {
        if (presentations == null) {
            this.presentations = new ArrayList<>();
        }
        return presentations;
    }

    public void setPresentationList(List<Presentation> p) {
        this.presentations = p;
    }

}
