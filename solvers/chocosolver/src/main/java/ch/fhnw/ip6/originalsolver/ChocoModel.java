package ch.fhnw.ip6.originalsolver;

import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.common.model.Model;
import org.chocosolver.solver.variables.BoolVar;

import java.util.List;

public class ChocoModel extends Model<org.chocosolver.solver.Model, BoolVar> {

    public ChocoModel(List<? extends P> presentations, List<? extends L> lecturers, List<? extends R> rooms, List<? extends T> timeslots, boolean[][] offTimes, org.chocosolver.solver.Model model) {
        super(presentations, lecturers, rooms, timeslots, offTimes, model);
    }

    @Override
    protected BoolVar[][][] setupVars() {
        BoolVar[][][] presRoomTime = new BoolVar[getPresentations().size()][getRooms().size()][getTimeslots().size()];
        for (T t : getTimeslots()) {
            for (R r : getRooms()) {
                for (P p : getPresentations()) {
                    if (!p.getType().equals(r.getType())) { // If roomtype doesnt fit
                        continue;
                    }
                    if (getOfftimes()[idxLec(p.getCoach())][indexOf(t)] || getOfftimes()[idxLec(p.getExpert())][indexOf(t)]) { // If coach is locked at this time
                        continue;
                    }
                    presRoomTime[indexOf(p)][indexOf(r)][indexOf(t)] = getModel().boolVar("presRoomTime_p" + p.getId() + "_r" + r.getId() + "_t" + t.getId());
                }
            }
        }
        return presRoomTime;
    }

    private int idxLec(L lecturer) {
        return getLecturers().indexOf(getLecturers().stream().filter(l -> l.getId() == lecturer.getId()).findFirst().get());
    }
}
