package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.ospp.model.Student;
import ch.fhnw.ip6.ospp.vo.StudentVO;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface StudentMapper {

    StudentVO fromEntityToVO(Student student);
}
