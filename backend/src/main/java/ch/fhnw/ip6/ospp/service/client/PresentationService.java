package ch.fhnw.ip6.ospp.service.client;

import ch.fhnw.ip6.ospp.model.Presentation;
import org.springframework.web.multipart.MultipartFile;

public interface PresentationService {

    void loadPresentation(MultipartFile input);

    Presentation addPresentation(Presentation presentation);

    Presentation readById(long id);

    Presentation readByNr(String nr);

    void deleteAll();
}
