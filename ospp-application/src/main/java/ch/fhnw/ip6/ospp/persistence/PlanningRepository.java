package ch.fhnw.ip6.ospp.persistence;

import ch.fhnw.ip6.ospp.model.Planning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface PlanningRepository extends JpaRepository<Planning, Long> {
}
