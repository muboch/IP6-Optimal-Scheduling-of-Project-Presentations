package ch.fhnw.ip6.ospp.service.client;

import ch.fhnw.ip6.ospp.model.Student;

public interface StudentService {

    Student addStudent(Student student);

    Student readById(long id);


}
