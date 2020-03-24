package ch.fhnw.ip6.ospp.service.client;

import ch.fhnw.ip6.ospp.model.Lecturer;
import org.springframework.web.multipart.MultipartFile;

public interface LecturerService {


    Lecturer addLecturer(Lecturer lecturer);

    Lecturer readById(long id);

    Lecturer readByInitials(String initials);


    void loadLecturer(MultipartFile file);

    void deleteAll();
}
