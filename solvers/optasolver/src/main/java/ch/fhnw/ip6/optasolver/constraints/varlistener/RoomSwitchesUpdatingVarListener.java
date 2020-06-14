package ch.fhnw.ip6.optasolver.constraints.varlistener;

import ch.fhnw.ip6.optasolver.model.Lecturer;
import ch.fhnw.ip6.optasolver.model.Room;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.List;


public class RoomSwitchesUpdatingVarListener implements VariableListener<Lecturer> {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Lecturer lecturer) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Lecturer lecturer) {

    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, Lecturer lecturer) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Lecturer lecturer) {
        List<Room> rooms = lecturer.getRooms();
        int last = rooms.size();
        if (last <= 1) {
            lecturer.setRoomSwitches(0);
            return;
        }

        Room currentRoom = rooms.get(last);
        Room previousRoom = rooms.get(last - 1);

        if (currentRoom.getId() != previousRoom.getId()) {
            lecturer.setRoomSwitches(lecturer.getRoomSwitches() + 1);
        }
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Lecturer lecturer) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Lecturer lecturer) {

    }
}
