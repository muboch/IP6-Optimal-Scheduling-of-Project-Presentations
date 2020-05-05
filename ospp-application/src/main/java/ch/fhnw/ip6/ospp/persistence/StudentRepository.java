package ch.fhnw.ip6.ospp.persistence;

import ch.fhnw.ip6.ospp.model.Student;
import ch.fhnw.ip6.ospp.vo.StudentVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface StudentRepository extends JpaRepository<Student, Long> {

    List<StudentVO> findAllProjectedBy();


}
