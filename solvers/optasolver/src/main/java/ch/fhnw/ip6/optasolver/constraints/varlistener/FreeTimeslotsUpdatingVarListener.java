package ch.fhnw.ip6.optasolver.constraints.varlistener;

import ch.fhnw.ip6.optasolver.model.Lecturer;
import ch.fhnw.ip6.optasolver.model.Presentation;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class FreeTimeslotsUpdatingVarListener implements VariableListener<Presentation> {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Presentation presentation) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Presentation presentation) {

    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, Presentation presentation) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Presentation presentation) {
updateFreeTimeslots(scoreDirector,presentation);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Presentation presentation) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Presentation presentation) {

    }
    private void updateFreeTimeslots(ScoreDirector scoreDirector, Presentation presentation) {
        List<Lecturer> coachAndExpert = new ArrayList<>();
        coachAndExpert.add((Lecturer) presentation.getCoach());
        coachAndExpert.add((Lecturer) presentation.getExpert());

        coachAndExpert.forEach(l -> {
            if (l.getPresentations() == null){return;}
            if (l.getPresentations().stream().map(p -> p.getTimeslot()).filter(t -> t == null).count() > 0){
                return;
            }
            Presentation firstPres = l.getPresentations().stream().min(Comparator.comparingInt(p -> p.getTimeslot().getId())).get(); // change timeslot.getId to timeslot.getOrder
            Presentation lastPres = l.getPresentations().stream().max(Comparator.comparingInt(p -> p.getTimeslot().getId())).get(); // change timeslot.getId to timeslot.getOrder
            Integer numPres = l.getPresentations().size();
            Integer freeTimeslots = (numPres - (numPres - (lastPres.getTimeslot().getId() - firstPres.getTimeslot().getId())));

            scoreDirector.beforeVariableChanged(l, "freeTimeslots");
            l.setFreeTimeslots(freeTimeslots);
            scoreDirector.afterVariableChanged(l, "freeTimeslots");
        });
    }


}
