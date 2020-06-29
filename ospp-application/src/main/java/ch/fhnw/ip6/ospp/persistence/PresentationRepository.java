package ch.fhnw.ip6.ospp.persistence;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface PresentationRepository extends JpaRepository<Presentation, Long> {

    List<Presentation> findByCoachOrExpert(Lecturer coach, Lecturer expert);
    List<Presentation> findByStudentOneOrStudentTwo(Student studentOne, Student studentTwo);


}
