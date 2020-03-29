package ch.fhnw.ip6.ospp.service.client;

import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PresentationService {

    void loadPresentation(MultipartFile input);

    PresentationVO addPresentation(Presentation presentation);

    PresentationVO readById(long id);

    PresentationVO readByNr(String nr);

    void deleteAll();

    List<PresentationVO> getAll();
}
