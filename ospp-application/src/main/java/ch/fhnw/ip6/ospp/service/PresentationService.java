package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.mapper.PresentationMapper;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.persistence.PresentationRepository;
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class PresentationService {

    private final PresentationRepository presentationRepository;
    private final PresentationMapper presentationMapper;

    public PresentationVO findById(Long id) {
        Optional<Presentation> byId = presentationRepository.findById(id);
        return byId.map(presentationMapper::fromEntityToVo).orElseThrow(EntityNotFoundException::new);
    }

    public List<PresentationVO> getAll() {
        return presentationRepository.findAllProjectedBy();
    }

    public PresentationVO save(PresentationVO presentationVO) {
        Presentation presentation = presentationMapper.fromVoToEntity(presentationVO);
        return presentationMapper.fromEntityToVo(presentationRepository.save(presentation));
    }

    public void delete(Long id) {
        presentationRepository.deleteById(id);
    }

    public void deleteAll() {
        presentationRepository.deleteAll();
    }


}
