package ch.fhnw.ip6.osfpp.persistence;

import ch.fhnw.ip6.osfpp.model.Presentation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface PresentationRepository extends JpaRepository<Presentation, Long> {



}
