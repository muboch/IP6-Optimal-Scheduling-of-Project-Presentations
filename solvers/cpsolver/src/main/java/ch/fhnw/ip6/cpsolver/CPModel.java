package ch.fhnw.ip6.cpsolver;

import ch.fhnw.ip6.common.dto.Lecturer;
import ch.fhnw.ip6.common.dto.Presentation;
import ch.fhnw.ip6.common.dto.Room;
import ch.fhnw.ip6.common.dto.Timeslot;
import ch.fhnw.ip6.common.model.Model;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;

import java.util.List;

public class CPModel extends Model<CpModel, IntVar> {

    public CPModel(List<Presentation> presentations, List<Lecturer> lecturers, List<Room> rooms, List<Timeslot> timeslots, boolean[][] locktimes, CpModel model) {
        super(presentations, lecturers, rooms, timeslots, locktimes, model);
    }

    @Override
    protected IntVar[][][] setupVars() {
        IntVar[][][] presRoomTime = new IntVar[getPresentations().size()][getRooms().size()][getTimeslots().size()];
        for (Timeslot t : getTimeslots()) {
            for (Room r : getRooms()) {
                for (Presentation p : getPresentations()) {
                    if (!p.getType().equals(r.getType())) { // If roomtype doesnt fit
                        continue;
                    }
                    if (getOfftimes()[p.getCoach().getId()][t.getId()] || getOfftimes()[p.getExpert().getId()][t.getId()]) { // If coach is locked at this time
                        continue;
                    }
                    presRoomTime[p.getId()][r.getId()][t.getId()] = getModel().newBoolVar("presRoomTime_p" + p.getId() + "_r" + r.getId() + "_t" + t.getId());
                }
            }
        }
        return presRoomTime;
    }
}
