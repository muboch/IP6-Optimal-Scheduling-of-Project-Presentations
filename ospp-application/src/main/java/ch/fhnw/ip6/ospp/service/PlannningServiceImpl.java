package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.api.SolverApi;
import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.*;
import ch.fhnw.ip6.ospp.event.SolveEvent;
import ch.fhnw.ip6.ospp.mapper.LecturerMapper;
import ch.fhnw.ip6.ospp.mapper.PresentationMapper;
import ch.fhnw.ip6.ospp.mapper.RoomMapper;
import ch.fhnw.ip6.ospp.mapper.TimeslotMapper;
import ch.fhnw.ip6.ospp.model.ExcelFile;
import ch.fhnw.ip6.ospp.persistence.PlanningRepository;
import ch.fhnw.ip6.ospp.service.client.*;
import ch.fhnw.ip6.ospp.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlannningServiceImpl extends AbstractService implements PlanningService {

    private final PresentationService presentationService;
    private final LecturerService lecturerService;
    private final RoomService roomService;
    private final TimeslotService timeslotService;

    private final PlanningRepository planningRepository;

    private final PresentationMapper presentationMapper;
    private final LecturerMapper lecturerMapper;
    private final RoomMapper roomMapper;
    private final TimeslotMapper timeslotMapper;

    private final ApplicationContext applicationContext;
    private final SolverContext solverContext;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Value("${ospp.solver}")
    private String solverName;

    @Value("${ospp.testmode}")
    private boolean testmode = true;

    private static String[] columns = {"Nr", "Titel", "Name", "Klasse", "Name 2", "Klasse 2", "Betreuer", "Experte", "Zeit", "Raum"};

    private boolean[][] createLocktimesMap(List<LecturerVO> lecturerVOs, int numberOfTimeslots) {

        boolean[][] locktimes = new boolean[lecturerVOs.size()][numberOfTimeslots];

        for (int l = 0; l < lecturerVOs.size(); l++) {
            for (int t = 0; t < numberOfTimeslots; t++) {
                int finalT = t;
                locktimes[l][t] = lecturerVOs.get(l).getLocktimes().stream().filter(timeslotVO -> timeslotVO.getExternalId() == finalT).findFirst().isPresent();
            }
        }

        return locktimes;
    }

    @Override
    public Planning plan() throws Exception {
        List<PresentationVO> presentationVOs = presentationService.getAll();
        List<LecturerVO> lecturerVOs = lecturerService.getAll();
        List<RoomVO> roomVOs = roomService.getAll();
        List<TimeslotVO> timeslotVOs = timeslotService.getAll();

        List<Presentation> presentations = presentationVOs.stream().map(presentationMapper::toDto).collect(Collectors.toList());
        List<Lecturer> lecturers = lecturerVOs.stream().map(lecturerMapper::toDto).collect(Collectors.toList());
        List<Room> rooms = roomVOs.stream().map(roomMapper::toDto).collect(Collectors.toList());
        List<Timeslot> timeslots = timeslotVOs.stream().map(timeslotMapper::toDto).collect(Collectors.toList());

        boolean[][] locktimes = createLocktimesMap(lecturerVOs, timeslotVOs.size());


        Planning planning;
        if (solverContext.isSolving()) {
            throw new Exception("Solver is already running.");
        }
        if (testmode) {
            planning = getSolver().testSolve();
        } else {
            planning = getSolver().solve(presentations, lecturers, rooms, timeslots, locktimes);
        }

        ExcelFile excelFile = transformToCsv(planning);

        ch.fhnw.ip6.ospp.model.Planning planningEntity = new ch.fhnw.ip6.ospp.model.Planning();
        planningEntity.setNr(String.valueOf(planning.getNr()));
        planningEntity.setData(excelFile.getContent());
        planningEntity.setName(excelFile.getName());
        planningRepository.save(planningEntity);

        return planning;
    }

    private ExcelFile transformToCsv(Planning planning) {
        try {
            String fileName = "Planning_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";

            Workbook workbook = new XSSFWorkbook();

            // Create a Sheet
            Sheet sheet = workbook.createSheet("Planung");

            // Create a Font for styling header cells
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);

            // Create a CellStyle with the font
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // Create a Row
            Row headerRow = sheet.createRow(0);

            // Create cells
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Create Other rows and cells with employees data
            AtomicInteger rowNum = new AtomicInteger(1);
            planning.getSolutions().stream().sorted(Comparator.comparing(o -> o.getTimeSlot().getDate())).forEach(solution -> {
                        Row row = sheet.createRow(rowNum.getAndIncrement());

                        row.createCell(0).setCellValue(solution.getPresentation().getNr());
                        row.createCell(1).setCellValue(solution.getPresentation().getTitle());
                        row.createCell(2).setCellValue(solution.getPresentation().getName());
                        row.createCell(3).setCellValue(solution.getPresentation().getSchoolclass());
                        row.createCell(4).setCellValue(solution.getPresentation().getName2());
                        row.createCell(5).setCellValue(solution.getPresentation().getSchoolclass2());
                        row.createCell(6).setCellValue(solution.getCoach().getName());
                        row.createCell(7).setCellValue(solution.getExpert().getName());
                        row.createCell(8).setCellValue(solution.getTimeSlot().getDate());
                        row.createCell(9).setCellValue(solution.getRoom().getName());
                    }

            );

            // Resize all columns to fit the content size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the output to a file
            FileOutputStream fileOut = new FileOutputStream(fileName);
            workbook.write(fileOut);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);

            byte[] content = bos.toByteArray();

            bos.close();
            fileOut.close();
            workbook.close();

            return ExcelFile.builder().name(fileName).content(content).build();

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }


    @Override
    public void firePlanning() throws Exception {
        if (solverContext.isSolving()) {
            throw new Exception("Solver is already running.");
        }
        applicationEventPublisher.publishEvent(new SolveEvent(this));
    }

    @Override
    public ExcelFile getFileById(long id) {
        ch.fhnw.ip6.ospp.model.Planning planning = planningRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No planning for id " + id));
        return ExcelFile.builder().name(planning.getName()).content(planning.getData()).build();
    }

    @Override
    public List<PlanningVO> getAllPlannings() {
        return planningRepository.findAllProjectedBy();
    }

    private SolverApi getSolver() {
        return (SolverApi) applicationContext.getBean(solverName);
    }


}
