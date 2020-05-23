package ch.fhnw.ip6.originalsolver;

import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.common.model.Model;

import java.util.List;

public class ChocoModel extends Model<Object, Object> {

    public ChocoModel(List<? extends P> presentations, List<? extends L> lecturers, List<? extends R> rooms, List<? extends T> timeslots, boolean[][] offTimes, Object model) {
        super(presentations, lecturers, rooms, timeslots, offTimes, model);
    }

    @Override
    protected Object[][][] setupVars() {
        return null;
    }
}
