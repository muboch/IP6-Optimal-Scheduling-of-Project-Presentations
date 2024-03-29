package ch.fhnw.ip6.ospp.persistence;

import ch.fhnw.ip6.ospp.model.Lecturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;


@Component
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {

    Optional<Lecturer> findByInitials(String initials);

}
