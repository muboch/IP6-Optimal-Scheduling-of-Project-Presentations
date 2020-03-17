package ch.fhnw.ip6.ospp.service.client;

import ch.fhnw.ip6.ospp.model.Teacher;
import org.springframework.web.multipart.MultipartFile;

public interface TeacherService {


    Teacher addTeacher(Teacher teacher);

    Teacher readById(long id);

    Teacher readByInitials(String initials);


    void loadTeachers(MultipartFile file);
}
