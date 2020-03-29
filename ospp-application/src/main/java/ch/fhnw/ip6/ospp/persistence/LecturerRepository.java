package ch.fhnw.ip6.ospp.persistence;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {

    Lecturer readByInitials(String initials);

    Lecturer readById(Long id);

    List<LecturerVO> findAllProjectedBy();
}
