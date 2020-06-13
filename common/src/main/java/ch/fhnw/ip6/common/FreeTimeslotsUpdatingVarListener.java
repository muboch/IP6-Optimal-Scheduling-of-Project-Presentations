package ch.fhnw.ip6.optasolver;

import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.PresentationDto;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.Comparator;


public class FreeTimeslotsUpdatingVarListener implements VariableListener<LecturerDto> {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, LecturerDto lecturerDto) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, LecturerDto lecturerDto) {

    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, LecturerDto lecturerDto) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, LecturerDto lecturerDto) {

        PresentationDto firstPres = lecturerDto.getPresentations().stream().min(Comparator.comparingInt(p -> p.getTimeslot().getId())).get(); // change timeslot.getId to timeslot.getOrder
        PresentationDto lastPres = lecturerDto.getPresentations().stream().max(Comparator.comparingInt(p -> p.getTimeslot().getId())).get(); // change timeslot.getId to timeslot.getOrder
        int numPres = lecturerDto.getPresentations().size();
        // Number of Free timeslots is = numPres - (lastPres - firstPres);
        lecturerDto.setFreeTimeslots(numPres - (lastPres.getTimeslot().getId() - firstPres.getTimeslot().getId()));
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, LecturerDto lecturerDto) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, LecturerDto lecturerDto) {

    }
}
