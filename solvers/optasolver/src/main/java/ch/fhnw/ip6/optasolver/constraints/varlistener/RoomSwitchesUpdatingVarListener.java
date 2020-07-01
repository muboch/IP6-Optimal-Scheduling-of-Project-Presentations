package ch.fhnw.ip6.optasolver.constraints.varlistener;

import ch.fhnw.ip6.optasolver.model.Lecturer;
import ch.fhnw.ip6.optasolver.model.Presentation;
import ch.fhnw.ip6.optasolver.model.Room;
import ch.fhnw.ip6.optasolver.model.Timeslot;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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

    private void updateRoomSwitches(ScoreDirector<Lecturer> scoreDirector, Presentation presentation) {
        Set<Lecturer> coachAndExpert = Set.of(presentation.getCoach(), presentation.getExpert());

        coachAndExpert.forEach(l -> {
            if (l.getPresentations() == null) {
                return;
            }
            if (l.getPresentations().stream().map(Presentation::getTimeslot).anyMatch(Objects::isNull)) {
                return;
            }
            if (l.getPresentations().stream().map(Presentation::getRoom).anyMatch(Objects::isNull)) {
                return;
            }
            List<Presentation> presentations = l.getPresentations().stream().filter(p -> p.getTimeslot() != null).sorted(Comparator.comparing(p -> p.getTimeslot().getSortOrder())).collect(Collectors.toList());

            Timeslot prevTimeslot = null;
            Room prevRoom = null;

            int roomSwitches = 0;

            for (Presentation p : presentations) {

                if (prevRoom == null) {
                    prevTimeslot = p.getTimeslot();
                    prevRoom = p.getRoom();
                }

                if (p.getTimeslot().getSortOrder() - prevTimeslot.getSortOrder() > 1 && prevRoom.getId() == p.getRoom().getId()) {
                    roomSwitches++;
                }
                if (prevRoom.getId() != p.getRoom().getId()) {
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
