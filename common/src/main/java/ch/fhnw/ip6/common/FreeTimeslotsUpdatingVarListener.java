package ch.fhnw.ip6.common;

import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.PresentationDto;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class FreeTimeslotsUpdatingVarListener implements VariableListener<PresentationDto> {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, PresentationDto lecturerDto) {

    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, PresentationDto lecturerDto) {
        updateFreeTimeslots(scoreDirector,lecturerDto);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, PresentationDto lecturerDto) {

    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, PresentationDto lecturerDto) {
        updateFreeTimeslots(scoreDirector,lecturerDto);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, PresentationDto lecturerDto) {

    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, PresentationDto presentationDto) {

    }

    private void updateFreeTimeslots(ScoreDirector scoreDirector, PresentationDto presentationDto) {
        List<LecturerDto> coachAndExpert = new ArrayList<>();
        coachAndExpert.add(presentationDto.getCoach());
        coachAndExpert.add(presentationDto.getExpert());

        coachAndExpert.forEach(l -> {
            List<PresentationDto> pres = l.getPresentations();


            if (l.getPresentations() == null){return;}
            //System.out.println(l.getPresentations().stream().map(p -> p.getTimeslot()).filter(t -> t == null).count() );
            if (l.getPresentations().stream().map(p -> p.getTimeslot()).filter(t -> t == null).count() > 0){
                return;
            }


            PresentationDto firstPres = l.getPresentations().stream().min(Comparator.comparingInt(p -> p.getTimeslot().getId())).get(); // change timeslot.getId to timeslot.getOrder
            PresentationDto lastPres = l.getPresentations().stream().max(Comparator.comparingInt(p -> p.getTimeslot().getId())).get(); // change timeslot.getId to timeslot.getOrder
            Integer numPres = l.getPresentations().size();
            // Number of Free timeslots is = numPres - (lastPres - firstPres);
            Integer freeTimeslots = (numPres - (numPres - (lastPres.getTimeslot().getId() - firstPres.getTimeslot().getId())));
            //System.out.println("Var changed:");
            //System.out.println(freeTimeslots);
                scoreDirector.beforeVariableChanged(l, "freeTimeslots");
                l.setFreeTimeslots(freeTimeslots);
                scoreDirector.afterVariableChanged(l, "freeTimeslots");
        });
    }
}