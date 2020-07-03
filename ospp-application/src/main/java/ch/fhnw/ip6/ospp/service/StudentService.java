package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Student;
import ch.fhnw.ip6.ospp.persistence.PresentationRepository;
import ch.fhnw.ip6.ospp.persistence.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final PresentationRepository presentationRepository;

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public void delete(Long id) {
        Optional<Student> student = studentRepository.findById(id);

        if (!student.isPresent()){
            throw new FachlicheException("Schüler existiert nicht.");
        }
        List<Presentation> presentations = presentationRepository.findByStudentOneOrStudentTwo(student.get(),student.get());
        if(presentations.size() > 0){
            throw new FachlicheException("Dem Schüler sind noch Präsentationen zugewiesen");
        }
        studentRepository.deleteById(id);
    }

    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }

    public void deleteAll() {
        studentRepository.deleteAllInBatch();
    }

    public List<Student> getAll() {
        return studentRepository.findAll();
    }
}
