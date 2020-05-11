package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.mapper.PresentationMapper;
import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Student;
import ch.fhnw.ip6.ospp.persistence.PresentationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class PresentationService {

    private final PresentationRepository presentationRepository;
    private final StudentService studentService;
    private final LecturerService lecturerService;

    public Optional<Presentation> findById(Long id) {
        return presentationRepository.findById(id);
    }

    public List<Presentation> getAll() {
        return presentationRepository.findAll();
    }

    public Presentation save(Presentation presentation) {
        if(presentation.getId() == 0){
            if(presentation.getStudentOne() != null) {
                Student studentOne = studentService.findById(presentation.getStudentOne().getId()).orElse(presentation.getStudentOne());
                presentation.setStudentOne(studentOne);
            }
            if(presentation.getStudentTwo() != null) {
                Student studentTwo = studentService.findById(presentation.getStudentTwo().getId()).orElse(presentation.getStudentTwo());
                presentation.setStudentTwo(studentTwo);
            }
            if(presentation.getCoach() != null) {
                Lecturer coach = lecturerService.findById(presentation.getCoach().getId()).orElse(presentation.getCoach());
                presentation.setCoach(coach);
            }
            if(presentation.getExpert() != null) {
                Lecturer expert = lecturerService.findById(presentation.getExpert().getId()).orElse(presentation.getExpert());
                presentation.setExpert(expert);

            }
        }
        return presentationRepository.save(presentation);
    }

    public void delete(Long id) {
        presentationRepository.deleteById(id);
    }

    public void deleteAll() {
        presentationRepository.deleteAll();
    }


}
