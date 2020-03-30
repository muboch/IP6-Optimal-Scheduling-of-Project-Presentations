package ch.fhnw.ip6.ospp.persistence;

import ch.fhnw.ip6.ospp.model.Planning;
import ch.fhnw.ip6.ospp.vo.PlanningVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PlanningRepository extends JpaRepository<Planning, Long> {

    List<PlanningVO> findAllProjectedBy();

}
