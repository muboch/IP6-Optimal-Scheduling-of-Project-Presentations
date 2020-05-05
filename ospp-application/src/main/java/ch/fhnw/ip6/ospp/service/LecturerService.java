package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.ospp.mapper.LecturerMapper;
import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.persistence.LecturerRepository;
import ch.fhnw.ip6.ospp.vo.LecturerVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class LecturerService extends AbstractService  {

    private final LecturerRepository lecturerRepository;
    private final LecturerMapper lecturerMapper;

    public Lecturer save(Lecturer room) {
        return lecturerRepository.save(room);
    }

    public LecturerVO findById(Long id) {
        Optional<Lecturer> byId = lecturerRepository.findById(id);
        return byId.map(lecturerMapper::fromEntityToVo).orElseThrow(EntityNotFoundException::new);
    }
    public Lecturer readByInitials(String initials) {
        return lecturerRepository.readByInitials(initials);
    }

    public void loadLecturer(MultipartFile input) {
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

                Lecturer expert = Lecturer.lecturerBuilder()
                        .initials(row.getCell(headerMap.get("initials")).getStringCellValue())
                        .email(row.getCell(headerMap.get("email")).getStringCellValue())
                        .lastname(row.getCell(headerMap.get("lastname")).getStringCellValue())
                        .firstname(row.getCell(headerMap.get("firstname")).getStringCellValue())
                        .externalId(Integer.parseInt(row.getCell(headerMap.get("id")).getStringCellValue()))
                        .build();
                lecturerRepository.save(expert);

            }

        } catch (IOException e) {
            log.error("An exception occured while parsing file {} [{}]", input.getOriginalFilename(), e.getMessage());
        }
    }

    public void deleteAll() {
        lecturerRepository.deleteAll();
    }

    public List<LecturerVO> getAll() {
        return lecturerRepository.findAllProjectedBy();
    }

    public LecturerVO saveLecturer(LecturerVO lecturer) {
        Lecturer lecturerEntity = lecturerRepository.save(lecturerMapper.fromVoToEntity(lecturer));
        return lecturerMapper.fromEntityToVo(lecturerEntity);
    }

    public void delete(Long id) {
        lecturerRepository.deleteById(id);
    }
}
