package ch.fhnw.ip6.ospp.mapper;

import ch.fhnw.ip6.ospp.model.Student;
import org.mapstruct.Mapper;

@Mapper
public interface StudentMapper {

    ch.fhnw.ip6.common.dto.Student toDto(Student student);

}
