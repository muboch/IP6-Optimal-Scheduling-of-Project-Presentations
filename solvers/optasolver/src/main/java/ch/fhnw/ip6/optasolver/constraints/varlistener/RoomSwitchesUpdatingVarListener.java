package ch.fhnw.ip6.optasolver.constraints.varlistener;

import ch.fhnw.ip6.optasolver.model.Lecturer;
import ch.fhnw.ip6.optasolver.model.Presentation;
import ch.fhnw.ip6.optasolver.model.Room;
import ch.fhnw.ip6.optasolver.model.Timeslot;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class RoomSwitchesUpdatingVarListener implements VariableListener<Presentation> {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Presentation presentation) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Presentation presentation) {
        updateRoomSwitches(scoreDirector, presentation);

    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, Presentation presentation) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Presentation presentation) {
        updateRoomSwitches(scoreDirector, presentation);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Presentation presentation) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Presentation presentation) {

    }

    private void updateRoomSwitches(ScoreDirector scoreDirector, Presentation presentation) {
        List<Lecturer> coachAndExpert = new ArrayList<>();
        coachAndExpert.add((Lecturer) presentation.getCoach());
        coachAndExpert.add((Lecturer) presentation.getExpert());

        coachAndExpert.forEach(l -> {
            if (l.getPresentations() == null) {
                scoreDirector.beforeVariableChanged(l, "roomSwitches");
                l.setRoomSwitches(0);
                scoreDirector.afterVariableChanged(l, "roomSwitches");
                return;
            }
            List<Presentation> presentations = l.getPresentations().stream().filter(p -> p.getTimeslot() != null).sorted(Comparator.comparing(p -> p.getTimeslot().getId())).collect(Collectors.toList());

            Timeslot prevTimeslot = null;
            Room prevRoom = null;

            int roomSwitches = 0;

            for (Presentation p : presentations) {

                if (prevRoom == null) {
                    prevTimeslot = p.getTimeslot();
                    prevRoom = p.getRoom();
                }

                if (prevRoom != p.getRoom() || p.getTimeslot() != null && p.getTimeslot().getId() - prevTimeslot.getId() > 1) {
                    roomSwitches++;
                }
                prevRoom = p.getRoom();
                prevTimeslot = p.getTimeslot();
            }

            scoreDirector.beforeVariableChanged(l, "roomSwitches");
            l.setRoomSwitches(roomSwitches);
            scoreDirector.afterVariableChanged(l, "roomSwitches");
        });
    }
}
