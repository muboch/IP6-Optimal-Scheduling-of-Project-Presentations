package ch.fhnw.ip6.originalsolver;

import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.common.model.Model;
import org.chocosolver.solver.variables.IntVar;

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
        IntVar[][] roomTime = new IntVar[getRooms().size()][getTimeslots().size()];

        Map<String, int[]> presIdMap = List.of("normal", "dance", "art", "music").stream().collect(Collectors.toMap(s -> s, this::getPresentationIds));

        for (T t : getTimeslots()) {
            for (R r : getRooms()) {
                for (P p : getPresentations()) {
                    roomTime[indexOf(r)][indexOf(t)] = getModel().intVar("roomTime_r" + r.getId() + "_t" + t.getId(), presIdMap.get(r.getType()));
                }
            }
        }
        return roomTime;
    }

    private int[] getPresentationIds(String presType) {
        List<Integer> n = getPresentations().stream().filter(r -> r.getType().equals(presType)).map(P::getId).collect(Collectors.toList());
        n.add(-1);
        return n.stream().mapToInt(i -> i).toArray();
    }

    private int idxLec(L lecturer) {
        return getLecturers().indexOf(getLecturers().stream().filter(l -> l.getId() == lecturer.getId()).findFirst().get());
    }
}
