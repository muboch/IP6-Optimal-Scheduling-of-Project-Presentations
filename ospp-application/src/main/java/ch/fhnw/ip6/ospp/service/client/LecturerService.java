package ch.fhnw.ip6.ospp.service.client;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LecturerService {

    Lecturer addLecturer(Lecturer lecturer);

    Lecturer readById(long id);

    LecturerVO readByExternalId(int id);

    Lecturer readByInitials(String initials);

    void loadLecturer(MultipartFile file);

    void deleteAll();

    List<LecturerVO> getAll();
}
