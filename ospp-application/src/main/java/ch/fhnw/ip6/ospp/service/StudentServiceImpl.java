package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.model.Student;
import ch.fhnw.ip6.ospp.persistence.StudentRepository;
import ch.fhnw.ip6.ospp.service.client.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    public Student addStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student readById(long id) {
        return studentRepository.readById(id);
    }
}
