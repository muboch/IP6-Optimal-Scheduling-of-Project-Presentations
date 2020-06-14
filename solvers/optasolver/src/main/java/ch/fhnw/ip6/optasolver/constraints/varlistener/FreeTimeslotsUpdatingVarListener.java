package ch.fhnw.ip6.optasolver.constraints.varlistener;

import ch.fhnw.ip6.optasolver.model.Lecturer;
import ch.fhnw.ip6.optasolver.model.Presentation;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.Comparator;


public class FreeTimeslotsUpdatingVarListener implements VariableListener<Lecturer> {

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

        Presentation firstPres = lecturer.getPresentations().stream().min(Comparator.comparingInt(p -> p.getTimeslot().getId())).get(); // change timeslot.getId to timeslot.getOrder
        Presentation lastPres = lecturer.getPresentations().stream().max(Comparator.comparingInt(p -> p.getTimeslot().getId())).get(); // change timeslot.getId to timeslot.getOrder
        int numPres = lecturer.getPresentations().size();
        // Number of Free timeslots is = numPres - (lastPres - firstPres);
        lecturer.setFreeTimeslots(numPres - (lastPres.getTimeslot().getId() - firstPres.getTimeslot().getId()));
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Lecturer lecturer) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Lecturer lecturer) {

    }
}
