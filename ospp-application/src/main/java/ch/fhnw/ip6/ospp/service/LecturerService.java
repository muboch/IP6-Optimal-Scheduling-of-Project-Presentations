package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.persistence.LecturerRepository;
import ch.fhnw.ip6.ospp.persistence.PresentationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LecturerService {

    private final LecturerRepository lecturerRepository;
    private final PresentationRepository presentationRepository;

    public Lecturer save(Lecturer lecturer) {
        return lecturerRepository.save(lecturer);
    }

    public void delete(Long id) {
        Optional<Lecturer> lecturer = lecturerRepository.findById(id);
        if (lecturer.isPresent()) {
            List<Presentation> presentations = presentationRepository.findByCoachOrExpert(lecturer.get(), lecturer.get());
            if (presentations.isEmpty()) {
                lecturerRepository.deleteById(id);
            } else {
                String joinedPresentations = presentations.stream().map(p -> String.valueOf(p.getId())).collect(Collectors.joining(","));
                throw new IllegalArgumentException(String.format("Lehrperson ist noch Präsention (%s) zugewiesen.", joinedPresentations));
            }
        }
    }

    public void deleteAll() {
        lecturerRepository.deleteAll();
    }

    public List<Lecturer> getAll() {
        return lecturerRepository.findAll();
    }

    public Optional<Lecturer> findById(Long id) {
        return lecturerRepository.findById(id);
    }

    public Optional<Lecturer> readByInitials(String initials) {
        return lecturerRepository.findByInitials(initials);
    }

}
