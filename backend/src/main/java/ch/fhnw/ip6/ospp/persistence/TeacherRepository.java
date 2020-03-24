package ch.fhnw.ip6.ospp.persistence;

import ch.fhnw.ip6.ospp.model.Teacher;
import ch.fhnw.ip6.ospp.vo.TeacherVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    List<TeacherVO> findAllProjectedBy();

    Teacher readByInitials(String initials);
}
