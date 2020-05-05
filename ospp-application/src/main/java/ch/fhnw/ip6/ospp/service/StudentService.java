package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.mapper.StudentMapper;
import ch.fhnw.ip6.ospp.model.Student;
import ch.fhnw.ip6.ospp.persistence.StudentRepository;
import ch.fhnw.ip6.ospp.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    public StudentVO save(StudentVO studentVO) {
        Student student = studentMapper.fromVoToEntity(studentVO);
        return studentMapper.fromEntityToVO(studentRepository.save(student));
    }

    public void delete(Long id){
        studentRepository.deleteById(id);
    }

    public StudentVO findById(Long id) {
        Optional<Student> byId = studentRepository.findById(id);
        return byId.map(studentMapper::fromEntityToVO).orElseThrow(EntityNotFoundException::new);
    }

    public List<StudentVO> getAll() {
        return studentRepository.findAllProjectedBy();
    }
}
