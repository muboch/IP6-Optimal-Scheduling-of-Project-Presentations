package ch.fhnw.ip6.ospp.service;

import ch.fhnw.ip6.api.SolverApi;
import ch.fhnw.ip6.api.SolverContext;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.ospp.event.SolveEvent;
import ch.fhnw.ip6.ospp.mapper.LecturerMapper;
import ch.fhnw.ip6.ospp.mapper.PresentationMapper;
import ch.fhnw.ip6.ospp.mapper.RoomMapper;
import ch.fhnw.ip6.ospp.mapper.TimeslotMapper;
import ch.fhnw.ip6.ospp.model.ExcelFile;
import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Room;
import ch.fhnw.ip6.ospp.model.Timeslot;
import ch.fhnw.ip6.ospp.persistence.LecturerRepository;
import ch.fhnw.ip6.ospp.persistence.PlanningRepository;
import ch.fhnw.ip6.ospp.persistence.PresentationRepository;
import ch.fhnw.ip6.ospp.persistence.RoomRepository;
import ch.fhnw.ip6.ospp.persistence.TimeslotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
public class PlanningService {

    private final PresentationRepository presentationRepository;
    private final LecturerRepository lecturerRepository;
    private final RoomRepository roomRepository;
    private final TimeslotRepository timeslotRepository;

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

    @Value("${ospp.testMode}")
    private boolean testMode = true;

    @Value("${ospp.timeLimit}")
    private int timeLimit;

    private static final String[] columns = {"Nr", "Titel", "Name", "Klasse", "Name 2", "Klasse 2", "Betreuer", "Experte", "Zeit", "Raum"};


    /**
     * Create a two-dimensional array of boolean. A field is true if the lecturer has a timeslot as offtime defined.
     *
     * @param lecturers
     * @param timeslots
     * @return
     */
    private boolean[][] createOffTimesMap(List<Lecturer> lecturers, List<Timeslot> timeslots) {

        boolean[][] offTimes = new boolean[lecturers.size()][timeslots.size()];

        for (int l = 0; l < lecturers.size(); l++) {
            List<Timeslot> offTimesOfLecturer = lecturers.get(l).getOfftimes();
            for (int t = 0; t < timeslots.size(); t++) {
                offTimes[l][t] = offTimesOfLecturer.contains(timeslots.get(t));
            }
        }

        return offTimes;
    }

    public Planning plan() throws Exception {
        List<Presentation> presentations = presentationRepository.findAll();
        List<Lecturer> lecturers = lecturerRepository.findAll();
        List<Room> rooms = roomRepository.findAll();
        List<Timeslot> timeslots = timeslotRepository.findAll();

        List<P> presentationDtos = presentations.stream().map(presentationMapper::fromEntityToDto).collect(Collectors.toList());
        List<L> lecturerDtos = lecturers.stream().map(lecturerMapper::fromEntityToDto).collect(Collectors.toList());
        List<R> roomDtos = rooms.stream().map(roomMapper::fromEntityToDto).collect(Collectors.toList());
        List<T> timeslotDtos = timeslots.stream().map(timeslotMapper::fromEntityToDto).collect(Collectors.toList());

        boolean[][] offTimes = createOffTimesMap(lecturers, timeslots);

        Planning planning;
        if (solverContext.isSolving()) {
            throw new Exception("Solver is already running.");
        }
        if (testMode) {
            solverContext.reset();
            planning = getSolver().testSolve();
        } else {
            solverContext.reset();
            planning = getSolver().solve(presentationDtos, lecturerDtos, roomDtos, timeslotDtos, offTimes);
        }

        ExcelFile excelFile = transformToCsv(planning);

        ch.fhnw.ip6.ospp.model.Planning planningEntity = new ch.fhnw.ip6.ospp.model.Planning();
        planningEntity.setNr(String.valueOf(planning.getNr()));
        planningEntity.setData(excelFile.getContent());
        planningEntity.setName(excelFile.getName());
        planningEntity.setStatus(planning.getStatus());
        planningEntity.setCreated(LocalDateTime.now());
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

    public void firePlanning() {
        if (solverContext.isSolving()) {
            throw new FachlicheException("Es wird bereits eine Planung erstellt.");
        }
        applicationEventPublisher.publishEvent(new SolveEvent(this));
    }

    public ExcelFile getFileById(long id) {
        ch.fhnw.ip6.ospp.model.Planning planning = planningRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("No planning for id " + id));
        return ExcelFile.builder().name(planning.getName()).content(planning.getData()).build();
    }

    public List<ch.fhnw.ip6.ospp.model.Planning> getAll() {
        return planningRepository.findAll();
    }

    private SolverApi getSolver() {
        return (SolverApi) applicationContext.getBean(solverName);
    }

    public void delete(Long id) {
        planningRepository.deleteById(id);
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    public void setSolverName(String solverName) {
        this.solverName = solverName;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }
}
