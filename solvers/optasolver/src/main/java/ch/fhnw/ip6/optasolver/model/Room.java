package ch.fhnw.ip6.optasolver.model;

import ch.fhnw.ip6.common.dto.RoomDto;
import ch.fhnw.ip6.optasolver.constraints.varlistener.FreeTimeslotsUpdatingVarListener;
import ch.fhnw.ip6.optasolver.constraints.varlistener.NumPresentationsUpdatingVarListener;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.InverseRelationShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

import java.util.ArrayList;
import java.util.List;

@PlanningEntity
public class Room extends RoomDto {

    private List<Presentation> presentations;

    @PlanningId
    public Long getPlanningId(){
        return (long) super.getId();
    }

    @InverseRelationShadowVariable(sourceVariableName = "room")
    public List<Presentation> getPresentationList() {
        if(presentations == null){this.presentations = new ArrayList<>();
        }
        return presentations;
    }

    public void setPresentationList(List<Presentation> p) {
    this.presentations = p;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
