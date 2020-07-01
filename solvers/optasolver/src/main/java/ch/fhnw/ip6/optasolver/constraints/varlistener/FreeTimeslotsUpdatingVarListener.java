package ch.fhnw.ip6.optasolver.constraints.varlistener;

import ch.fhnw.ip6.optasolver.model.Lecturer;
import ch.fhnw.ip6.optasolver.model.Presentation;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class FreeTimeslotsUpdatingVarListener implements VariableListener<Presentation> {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, Presentation presentation) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, Presentation presentation) {
        updateFreeTimeslots(scoreDirector, presentation);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, Presentation presentation) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, Presentation presentation) {
        updateFreeTimeslots(scoreDirector, presentation);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, Presentation presentation) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, Presentation presentation) {

    }

    private void updateFreeTimeslots(ScoreDirector scoreDirector, Presentation presentation) {
        List<Lecturer> coachAndExpert = new ArrayList<>();
        coachAndExpert.add(presentation.getCoach());
        coachAndExpert.add(presentation.getExpert());

        coachAndExpert.forEach(l -> {
            if (l.getPresentations() == null) {
                return;
            }
            if (l.getPresentations().stream().map(Presentation::getTimeslot).anyMatch(Objects::isNull)) {
                return;
            }
            Presentation firstPres = l.getPresentations().stream().min(Comparator.comparingInt(p -> p.getTimeslot().getSortOrder())).get();
            Presentation lastPres = l.getPresentations().stream().max(Comparator.comparingInt(p -> p.getTimeslot().getSortOrder())).get();
            int numPres = l.getPresentations().size();
            int freeTimeslots = lastPres.getTimeslot().getSortOrder() - firstPres.getTimeslot().getSortOrder() - numPres + 1;
            if(freeTimeslots < 0){
                freeTimeslots = 999;
            }

            scoreDirector.beforeVariableChanged(l, "freeTimeslots");
            l.setFreeTimeslots(freeTimeslots);
            scoreDirector.afterVariableChanged(l, "freeTimeslots");
        });
    }


}
