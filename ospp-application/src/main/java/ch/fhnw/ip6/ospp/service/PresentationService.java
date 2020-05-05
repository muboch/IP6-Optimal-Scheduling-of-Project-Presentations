package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.mapper.PresentationMapper;
import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Student;
import ch.fhnw.ip6.ospp.persistence.PresentationRepository;
import ch.fhnw.ip6.ospp.vo.PresentationVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Component
@RequiredArgsConstructor
public class PresentationService extends AbstractService {

    private final PresentationRepository presentationRepository;
    private final LecturerService lecturerService;
    private final PresentationMapper presentationMapper;

    public PresentationVO findById(Long id) {
        Optional<Presentation> byId = presentationRepository.findById(id);
        return byId.map(presentationMapper::fromEntityToVo).orElseThrow(EntityNotFoundException::new);
    }

    public List<PresentationVO> getAll() {
        return presentationRepository.findAllProjectedBy();
    }

    public PresentationVO save(PresentationVO presentationVO) {
        Presentation presentation = presentationMapper.fromVoToEntity(presentationVO);
        return presentationMapper.fromEntityToVo(presentationRepository.save(presentation));
    }

    public void delete(Long id){
        presentationRepository.deleteById(id);
    }

    public void deleteAll() {
        presentationRepository.deleteAll();
    }

    public void loadPresentation(MultipartFile input) {

        try {
            deleteAll();

            XSSFWorkbook wb = new XSSFWorkbook(input.getInputStream());
            XSSFSheet sheet = wb.getSheetAt(0);

            final Map<String, Integer> headerMap = new HashMap<>();

            for (Row row : sheet) {

                if (row.getRowNum() == 0) {
                    createHeaderIndexMap(row, headerMap);
                    continue;
                }


                Lecturer coach = lecturerService.readByInitials(row.getCell(headerMap.get("coachInitials")).getStringCellValue());
                Lecturer expert = lecturerService.readByInitials(row.getCell(headerMap.get("expertInitials")).getStringCellValue());

                Student studentOne = Student.studentBuilder()
                        .name(row.getCell(headerMap.get("name")).getStringCellValue())
                        .schoolclass(row.getCell(headerMap.get("schoolclass")).getStringCellValue())
                        .build();

                Presentation presentation = Presentation.builder()
                        .nr(row.getCell(headerMap.get("nr")).getStringCellValue())
                        .externalId(Integer.parseInt(row.getCell(headerMap.get("id")).getStringCellValue()))
                        .title(row.getCell(headerMap.get("title")).getStringCellValue())
                        .type(row.getCell(headerMap.get("type")).getStringCellValue())
                        .studentOne(studentOne)
                        .expert(expert)
                        .coach(coach)
                        .build();

                if (StringUtils.isNotEmpty(row.getCell(headerMap.get("name2")).getStringCellValue())) {
                    Student studentTwo = Student.studentBuilder()
                            .name(row.getCell(headerMap.get("name2")).getStringCellValue())
                            .schoolclass(row.getCell(headerMap.get("schoolclass")).getStringCellValue())
                            .build();
                    presentation.setStudentTwo(studentTwo);
                }

                presentationRepository.save(presentation);
            }
        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
    }


}
