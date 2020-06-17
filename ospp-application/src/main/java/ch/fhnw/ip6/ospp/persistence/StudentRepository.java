package ch.fhnw.ip6.ospp.persistence;

import ch.fhnw.ip6.ospp.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface StudentRepository extends JpaRepository<Student, Long> {


}
