package ch.fhnw.ip6.ortoolssolver;

import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.common.model.Model;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;

import java.util.List;

public class OrToolsModel extends Model<CpModel, IntVar> {

    public OrToolsModel(List<? extends P> presentations, List<? extends L> lecturers, List<? extends R> rooms, List<? extends T> timeslots, boolean[][] offTimes, CpModel model) {
        super(presentations, lecturers, rooms, timeslots, offTimes, model);
    }

    @Override
    protected IntVar[][][] setupVars() {
        IntVar[][][] presRoomTime = new IntVar[getPresentations().size()][getRooms().size()][getTimeslots().size()];
        for (T t : getTimeslots()) {
            for (R r : getRooms()) {
                for (P p : getPresentations()) {
                    if (!p.getType().equals(r.getType())) { // If roomtype doesnt fit
                        continue;
                    }
                    if (getOfftimes()[idxLec(p.getCoach())][indexOf(t)] || getOfftimes()[idxLec(p.getExpert())][indexOf(t)]) { // If coach is locked at this time
                        continue;
                    }
                    presRoomTime[indexOf(p)][indexOf(r)][indexOf(t)] = getModel().newBoolVar("presRoomTime_p" + p.getId() + "_r" + r.getId() + "_t" + t.getId());
                }
            }
        }
        return presRoomTime;
    }

    @Override
    protected IntVar[][] setupVars2d() {
        return new IntVar[0][];
    }

    public IntVar[][][] getPresRoomTime() {
        return getX();
    }

    private int idxLec(L lecturer) {
        return getLecturers().indexOf(getLecturers().stream().filter(l -> l.getId() == lecturer.getId()).findFirst().get());
    }
}
