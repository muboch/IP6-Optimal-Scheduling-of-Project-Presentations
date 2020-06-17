package ch.fhnw.ip6.optasolver.model;

import ch.fhnw.ip6.common.dto.TimeslotDto;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;

import java.util.ArrayList;
import java.util.List;

@PlanningEntity
public class Timeslot extends TimeslotDto {

    private List<Presentation> presentations;


    @InverseRelationShadowVariable(sourceVariableName = "timeslot")
    public List<Presentation> getPresentationList() {
        if(presentations == null){this.presentations = new ArrayList<>();
        }
        return presentations;
    }

    public void setPresentationList(List<Presentation> p) {
        this.presentations = p;
    }

}
