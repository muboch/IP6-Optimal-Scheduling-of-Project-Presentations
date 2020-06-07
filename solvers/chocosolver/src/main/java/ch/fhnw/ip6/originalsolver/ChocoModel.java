package ch.fhnw.ip6.originalsolver;

import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.common.model.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChocoModel extends Model<org.chocosolver.solver.Model, IntVar> {

    public ChocoModel(List<? extends P> presentations, List<? extends L> lecturers, List<? extends R> rooms, List<? extends T> timeslots, boolean[][] offTimes, org.chocosolver.solver.Model model) {
        super(presentations, lecturers, rooms, timeslots, offTimes, model);
    }

    @Override
    protected IntVar[][][] setupVars() {
        return new IntVar[0][][];
    }

    protected IntVar[][] setupVars2d() {
        IntVar[][] presRoomTime = new IntVar[getPresentations().size()][getTimeslots().size()];



        Map roomIdMap = new HashMap<String,int[]>();
        roomIdMap.put("normal", getRoomIDs("normal"));
        roomIdMap.put("dance", getRoomIDs("dance"));
        roomIdMap.put("art", getRoomIDs("art"));
        roomIdMap.put("music", getRoomIDs("music"));


        for (T t : getTimeslots()) {
            for (R r : getRooms()) {
                for (P p : getPresentations()) {
                    if (!p.getType().equals(r.getType())) { // If roomtype doesnt fit
                        continue;
                    }
                    if (getOfftimes()[idxLec(p.getCoach())][indexOf(t)] || getOfftimes()[idxLec(p.getExpert())][indexOf(t)]) { // If coach is locked at this time
                        continue;
                    }
                    int [] roomIDS = (int[]) roomIdMap.get(p.getType());

                    presRoomTime[indexOf(p)][indexOf(t)] = getModel().intVar("presRoomTime_p" + p.getId() +  "_t" + t.getId(), (int[]) roomIdMap.get(p.getType()));
                }
            }
        }
        return presRoomTime;
    }

    private int[] getRoomIDs(String roomType) {
        List<Integer> n =  getRooms().stream().filter(r -> r.getType().equals(roomType)).map(r -> r.getId()).collect(Collectors.toList());
        n.add(-1);
        return n.stream().mapToInt(i->i).toArray();
    }

    private int idxLec(L lecturer) {
        return getLecturers().indexOf(getLecturers().stream().filter(l -> l.getId() == lecturer.getId()).findFirst().get());
    }
}
