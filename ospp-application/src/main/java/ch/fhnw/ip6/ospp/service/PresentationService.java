package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Student;
import ch.fhnw.ip6.ospp.persistence.LecturerRepository;
import ch.fhnw.ip6.ospp.persistence.PresentationRepository;
import ch.fhnw.ip6.ospp.persistence.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Log4j2
@Component
@RequiredArgsConstructor
public class PresentationService {

    private final PresentationRepository presentationRepository;
    private final StudentRepository studentRepository;
    private final LecturerRepository lecturerRepository;

    public Optional<Presentation> findById(Long id) {
        return presentationRepository.findById(id);
    }

    public List<Presentation> getAll() {
        return presentationRepository.findAll();
    }

    public Presentation save(Presentation presentation) {
        if (presentation.getId() == 0) {
            if (presentation.getStudentOne() != null) {
                Student studentOne = studentRepository.findById(presentation.getStudentOne().getId()).orElse(presentation.getStudentOne());
                presentation.setStudentOne(studentOne);
            } else {
                throw new FachlicheException(String.format("Präsentation (%s): Schüler 1 muss vorhanden sein.", presentation.getExternalId()));
            }
            if (presentation.getStudentTwo() != null) {
                Student studentTwo = studentRepository.findById(presentation.getStudentTwo().getId()).orElse(presentation.getStudentTwo());
                presentation.setStudentTwo(studentTwo);
            }
            if (Objects.equals(presentation.getStudentOne(), presentation.getStudentTwo())) {
                throw new FachlicheException(String.format("Präsentation (%s): Schüler 1 und Schüler 2 dürfen nicht identisch sein.", presentation.getExternalId()));
            }
            if (presentation.getCoach() != null) {
                Lecturer coach = lecturerRepository.findById(presentation.getCoach().getId()).orElse(presentation.getCoach());
                presentation.setCoach(coach);
            }
            if (presentation.getExpert() != null) {
                Lecturer expert = lecturerRepository.findById(presentation.getExpert().getId()).orElse(presentation.getExpert());
                presentation.setExpert(expert);
            }
            if (Objects.equals(presentation.getCoach(), presentation.getExpert())) {
                throw new FachlicheException(String.format("Präsentation (%s): Coach und Experte dürfen nicht identisch sein.", presentation.getExternalId()));
            }
        }
        return presentationRepository.save(presentation);
    }

    public void delete(Long id) {
        presentationRepository.deleteById(id);
    }

    public void deleteAll() {
        presentationRepository.deleteAll();
    }

    public List<Presentation> findByLecturer(Lecturer lecturer) {
        return presentationRepository.findByCoachOrExpert(lecturer, lecturer);
    }
}
