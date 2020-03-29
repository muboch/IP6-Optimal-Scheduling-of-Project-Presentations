package ch.fhnw.ip6.ospp.service.client;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LecturerService {

    Lecturer addLecturer(Lecturer lecturer);

    Lecturer readById(long id);

    Lecturer readByInitials(String initials);

    void loadLecturer(MultipartFile file);

    void deleteAll();

    List<LecturerVO> getAll();
}
