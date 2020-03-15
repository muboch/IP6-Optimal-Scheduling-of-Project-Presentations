package ch.fhnw.ip6.ospp.persistence;

import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PresentationRepository extends JpaRepository<Presentation, Long> {

    List<PresentationVO> findAllProjectedBy();

}
