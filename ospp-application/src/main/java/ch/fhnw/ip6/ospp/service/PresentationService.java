package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.mapper.PresentationMapper;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.persistence.PresentationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class PresentationService {

    private final PresentationRepository presentationRepository;

    public Optional<Presentation> findById(Long id) {
        return presentationRepository.findById(id);
    }

    public List<Presentation> getAll() {
        return presentationRepository.findAll();
    }

    public Presentation save(Presentation presentation) {
        return presentationRepository.save(presentation);
    }

    public void delete(Long id) {
        presentationRepository.deleteById(id);
    }

    public void deleteAll() {
        presentationRepository.deleteAll();
    }


}
