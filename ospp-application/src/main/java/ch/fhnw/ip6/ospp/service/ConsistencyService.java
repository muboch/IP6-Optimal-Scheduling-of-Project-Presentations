package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Timeslot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.fhnw.ip6.ospp.service.ConsistencyError.Status.ERROR;
import static ch.fhnw.ip6.ospp.service.ConsistencyError.Status.WARN;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsistencyService {

    public List<ConsistencyError> checkConsistencyOfPresentations(Set<Presentation> presentations) {

        List<ConsistencyError> errors = new ArrayList<>();

        for (Presentation p : presentations) {
            List<String> missingObjects = new ArrayList<>();
            if (p.getStudentOne() == null) {
                missingObjects.add("Schüler");
            }
            if (p.getCoach() == null) {
                missingObjects.add("Betreuer");
            }
            if (p.getExpert() == null) {
                missingObjects.add("Experte");
            }
            if (!missingObjects.isEmpty())
                errors.add(new ConsistencyError(ERROR, String.format("Bei Präsentation %s fehlt %s", p.getNr(), StringUtils.joinWith(",", missingObjects))));
        }
        return errors;
    }

    public List<ConsistencyError> checkConsistencyOfLecturers(Set<Presentation> presentations, Set<Lecturer> lecturers, Set<Lecturer> offtimesLecturers) {

        Map<String, Lecturer> lecturerMap = lecturers.stream().collect(Collectors.toMap(Lecturer::getInitials, l -> l));

        Set<Lecturer> coaches = presentations.stream().map(Presentation::getCoach).collect(Collectors.toSet());
        Set<Lecturer> experts = presentations.stream().map(Presentation::getExpert).collect(Collectors.toSet());
        Set<Lecturer> lecturersOfPresentations = new HashSet<>();
        lecturersOfPresentations.addAll(coaches);
        lecturersOfPresentations.addAll(experts);
        Map<String, Lecturer> lecturersOfPresentationsMap = lecturersOfPresentations.stream().collect(Collectors.toMap(Lecturer::getInitials, l -> l));

        List<ConsistencyError> errors = new ArrayList<>();

        for (Lecturer l : lecturersOfPresentations) {
            if (lecturerMap.get(l.getInitials()) == null) {
                errors.add(new ConsistencyError(ERROR, String.format("Lehrerperson mit Kürzel '%s' ist nicht in der Liste aller Lehrerpersonen.", l.getInitials())));
            }
        }

        for (Lecturer l : lecturers) {
            if (lecturersOfPresentationsMap.get(l.getInitials()) == null) {
                errors.add(new ConsistencyError(WARN, String.format("Lehrerperson mit Kürzel '%s' ist keiner Präsentation zugewiesen.", l.getInitials())));
            }
        }

        for (Lecturer l : offtimesLecturers) {
            if (lecturerMap.get(l.getInitials()) == null) {
                errors.add(new ConsistencyError(ERROR, String.format("Lehrerperson mit Kürzel '%s' ist in den Sperrzeiten erfasst, nicht aber in der Liste aller Lehrerpersonen.", l.getInitials())));
            }
        }

        return errors;
    }

    public List<ConsistencyError> checkConsistencyOfTimeslots(Set<Timeslot> timeslots, Set<Timeslot> offTimes) {

        Map<String, Timeslot> timeslotsMap = timeslots.stream().collect(Collectors.toMap(Timeslot::getDate, t -> t));

        List<ConsistencyError> errors = new ArrayList<>();

        for (Timeslot offtime : offTimes) {
            if (timeslotsMap.get(offtime.getDate()) == null) {
                errors.add(new ConsistencyError(ERROR, String.format("Die Sperrzeit '%s' ist nicht in der Liste der Zeitlots.", offtime.getDate())));

            }
        }

        return errors;
    }

}
