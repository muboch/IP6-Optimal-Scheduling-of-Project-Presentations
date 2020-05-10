package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.mapper.LecturerMapper;
import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.persistence.LecturerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LecturerService {

    private final LecturerRepository lecturerRepository;

    public Lecturer save(Lecturer lecturer) {
        return lecturerRepository.save(lecturer);
    }

    public void delete(Long id) {
        lecturerRepository.deleteById(id);
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
