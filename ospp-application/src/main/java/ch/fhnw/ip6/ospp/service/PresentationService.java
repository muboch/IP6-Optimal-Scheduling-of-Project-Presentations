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
        if (presentation.getId() == 0) { // If we create new Presentation
            validateAdd(presentation);
        } else {
            validateUpdate(presentation);
        }
        return presentationRepository.save(presentation);
    }

    private void validateUpdate(Presentation presentation) {
        if (presentation.getStudentOne() != null) {
            Student studentOne = studentRepository.findById(presentation.getStudentOne().getId()).orElse(presentation.getStudentOne());
            if (presentationRepository.findByStudentOneOrStudentTwo(studentOne, studentOne).size() >= 1) {
                throw new FachlicheException(String.format("Präsentation (%s): Schüler 1 darf nicht mehr als einer Präsentation zugewiesen werden.", presentation.getExternalId()));
            }
            presentation.setStudentOne(studentOne);
        } else {
            throw new FachlicheException(String.format("Präsentation (%s): Schüler 1 muss vorhanden sein.", presentation.getExternalId()));
        }

        if (presentation.getStudentTwo() != null) {
            Student studentTwo = studentRepository.findById(presentation.getStudentTwo().getId()).orElse(presentation.getStudentTwo());
            if (presentationRepository.findByStudentOneOrStudentTwo(studentTwo, studentTwo).size() >= 1) {
                throw new FachlicheException(String.format("Präsentation (%s): Schüler 2 darf nicht mehr als einer Präsentation zugewiesen werden.", presentation.getExternalId()));
            }
            presentation.setStudentTwo(studentTwo);
        }
        if (Objects.equals(presentation.getStudentOne(), presentation.getStudentTwo())) {
            throw new FachlicheException(String.format("Präsentation (%s): Schüler 1 und Schüler 2 dürfen nicht identisch sein.", presentation.getExternalId()));
        }
        if (presentation.getCoach() != null) {
            Lecturer coach = lecturerRepository.findById(presentation.getCoach().getId()).orElse(presentation.getCoach());
            presentation.setCoach(coach);
        } else {
            throw new FachlicheException("Der Präsentation muss ein Dozent zugewiesen werden");
        }
        if (presentation.getExpert() != null) {
            Lecturer expert = lecturerRepository.findById(presentation.getExpert().getId()).orElse(presentation.getExpert());
            presentation.setExpert(expert);
        } else {
            throw new FachlicheException("Der Präsentation muss ein Experte zugewiesen werden");
        }
        if (Objects.equals(presentation.getCoach(), presentation.getExpert())) {
            throw new FachlicheException(String.format("Präsentation (%s): Coach und Experte dürfen nicht identisch sein.", presentation.getExternalId()));
        }
    }

    private void validateAdd(Presentation presentation) {
        // check if studentOne exists and has only 1 pres
        if (presentation.getStudentOne() != null) {
            Student studentOne = studentRepository.findById(presentation.getStudentOne().getId()).orElse(presentation.getStudentOne());
            if (studentOne.getId() != 0 && presentationRepository.findByStudentOneOrStudentTwo(studentOne, studentOne).size() >= 1) {
                throw new FachlicheException(String.format("Präsentation (%s): Schüler 1 darf nicht mehr als einer Präsentation zugewiesen werden.", presentation.getExternalId()));
            }
            presentation.setStudentOne(studentOne);
        } else {
            throw new FachlicheException(String.format("Präsentation (%s): Schüler 1 muss vorhanden sein.", presentation.getExternalId()));
        }
        // check if studentTwo has only 1 pres
        if (presentation.getStudentTwo() != null) {
            Student studentTwo = studentRepository.findById(presentation.getStudentTwo().getId()).orElse(presentation.getStudentTwo());
            if (studentTwo.getId() != 0 && presentationRepository.findByStudentOneOrStudentTwo(studentTwo, studentTwo).size() >= 1) {
                throw new FachlicheException(String.format("Präsentation (%s): Schüler 2 darf nicht mehr als einer Präsentation zugewiesen werden.", presentation.getExternalId()));
            }
            presentation.setStudentTwo(studentTwo);
        }
        // check if StudentOne and StudentTwo are same.
        if (Objects.equals(presentation.getStudentOne(), presentation.getStudentTwo())) {
            throw new FachlicheException(String.format("Präsentation (%s): Schüler 1 und Schüler 2 dürfen nicht identisch sein.", presentation.getExternalId()));
        }
        if (presentation.getCoach() != null) {
            Lecturer coach = lecturerRepository.findById(presentation.getCoach().getId()).orElse(presentation.getCoach());
            presentation.setCoach(coach);
        } else {
            throw new FachlicheException("Der Präsentation muss ein Dozent zugewiesen werden");
        }
        if (presentation.getExpert() != null) {
            Lecturer expert = lecturerRepository.findById(presentation.getExpert().getId()).orElse(presentation.getExpert());
            presentation.setExpert(expert);
        } else {
            throw new FachlicheException("Der Präsentation muss ein Experte zugewiesen werden");

        }
        if (Objects.equals(presentation.getCoach(), presentation.getExpert())) {
            throw new FachlicheException(String.format("Präsentation (%s): Coach und Experte dürfen nicht identisch sein.", presentation.getExternalId()));
        }
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
