package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.mapper.LecturerMapper;
import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.persistence.LecturerRepository;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LecturerService {

    private final LecturerRepository lecturerRepository;
    private final LecturerMapper lecturerMapper;

    public LecturerVO save(Lecturer lecturer) {
        return lecturerMapper.fromEntityToVo(lecturerRepository.save(lecturer));
    }

    public void delete(Long id) {
        lecturerRepository.deleteById(id);
    }

    public void deleteAll() {
        lecturerRepository.deleteAll();
    }

    public List<LecturerVO> getAll() {
        return lecturerRepository.findAllProjectedBy();
    }

    public LecturerVO findById(Long id) {
        Optional<Lecturer> byId = lecturerRepository.findById(id);
        return byId.map(lecturerMapper::fromEntityToVo).orElseThrow(EntityNotFoundException::new);
    }

    public Lecturer readByInitials(String initials) {
        return lecturerRepository.readByInitials(initials);
    }

}
