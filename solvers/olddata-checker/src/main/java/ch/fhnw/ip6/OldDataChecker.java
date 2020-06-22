package ch.fhnw.ip6;

import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.Planning;
import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.common.dto.RoomDto;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.StatusEnum;
import ch.fhnw.ip6.common.dto.TimeslotDto;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.solutionchecker.SolutionChecker;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OldDataChecker {

    public static void main(String[] args) {


        HashMap<Integer, Integer> timeslotCostMap = new HashMap<>();
        timeslotCostMap.put(0,50);
        timeslotCostMap.put(1,45);
        timeslotCostMap.put(2,40);
        timeslotCostMap.put(3,40);
        timeslotCostMap.put(4,35);
        timeslotCostMap.put(5,30);
        timeslotCostMap.put(6,30);
        timeslotCostMap.put(7,50);
        timeslotCostMap.put(8,35);
        timeslotCostMap.put(9,40);
        timeslotCostMap.put(10,40);
        timeslotCostMap.put(11,45);
        timeslotCostMap.put(12,50); // FR 17:15
        timeslotCostMap.put(13,55); // FR 18:00 -> Different than new timeslots
        timeslotCostMap.put(14,50); // SA 08:00 -> Different than new timeslots
        timeslotCostMap.put(15,45); // SA 0845
        timeslotCostMap.put(16,45); // SA 0900
        timeslotCostMap.put(17,40); // SA 0930
        timeslotCostMap.put(18,40); //SA 1000
        timeslotCostMap.put(19,45); //SA 1015
        timeslotCostMap.put(20,50); //SA 1100



        try (InputStream inputStream = OldDataChecker.class.getClassLoader().getResourceAsStream("Präsentationsplan_SA_19_def.xlsx")) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            DataFormatter dataFormatter = new DataFormatter();
            Sheet sheet = workbook.getSheetAt(0);

            Planning planning = new Planning();

            AtomicInteger presId = new AtomicInteger();

            // Timeslots
            AtomicInteger timeId = new AtomicInteger();
            Set<String> timeslots = new LinkedHashSet<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String date = dataFormatter.formatCellValue(row.getCell(8));
                timeslots.add(date.trim());
            }

            Map<String, TimeslotDto> ts = timeslots.stream().map(date -> {
                TimeslotDto timeslot = new TimeslotDto();
                timeslot.setDate(date);
                timeslot.setId(timeId.get());
                timeslot.setPriority(timeslotCostMap.get(timeId.getAndIncrement()));

                return timeslot;
            }).collect(Collectors.toMap(TimeslotDto::getDate, t -> t));

            // Lecturers
            AtomicInteger lecId = new AtomicInteger();
            Set<String> lecturers = new HashSet<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String ini1 = dataFormatter.formatCellValue(row.getCell(6));
                String ini2 = dataFormatter.formatCellValue(row.getCell(7));
                lecturers.add(ini1.trim());
                lecturers.add(ini2.trim());
            }
            Map<String, LecturerDto> ls = lecturers.stream().map(ini -> {
                LecturerDto lec = new LecturerDto();
                lec.setInitials(ini.toLowerCase().trim());
                lec.setId(lecId.incrementAndGet());
                return lec;
            }).collect(Collectors.toMap(LecturerDto::getInitials, lecturerDto -> lecturerDto));

            // Rooms
            AtomicInteger roomId = new AtomicInteger();
            Set<String> rooms = new HashSet<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                String name = dataFormatter.formatCellValue(row.getCell(9));
                rooms.add(name.trim());
            }

            Map<String, RoomDto> rs = rooms.stream().map(name -> {
                RoomDto room = new RoomDto();
                room.setName(name);
                room.setId(roomId.incrementAndGet());
                return room;
            }).collect(Collectors.toMap(RoomDto::getName, room -> room));


            // Presentations
            Set<Solution> solutions = new HashSet<>();
            List<PresentationDto> presentations = new ArrayList<>();
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Solution solution = new Solution();
                PresentationDto presentation = new PresentationDto();
                presentation.setId(presId.incrementAndGet());
                presentation.setNr(String.valueOf(presId.get()));
                presentation.setName(dataFormatter.formatCellValue(row.getCell(1)));
                presentation.setSchoolclass(dataFormatter.formatCellValue(row.getCell(2)));

                presentation.setName2(dataFormatter.formatCellValue(row.getCell(3)));
                presentation.setSchoolclass2(dataFormatter.formatCellValue(row.getCell(4)));
                presentation.setTitle(dataFormatter.formatCellValue(row.getCell(5)));
                String coachInitials = dataFormatter.formatCellValue(row.getCell(6)).toLowerCase();
                presentation.setCoachInitials(coachInitials.trim());
                presentation.setCoach(ls.get(presentation.getCoachInitials()));
                String expertInitials = dataFormatter.formatCellValue(row.getCell(7)).toLowerCase();
                presentation.setExpertInitials(expertInitials.trim());
                presentation.setExpert(ls.get(presentation.getExpertInitials()));
                presentations.add(presentation);

                solution.setCoach(ls.get(presentation.getCoachInitials()));
                solution.setExpert(ls.get(presentation.getExpertInitials()));
                solution.setPresentation(presentation);
                String room = dataFormatter.formatCellValue(row.getCell(9));
                solution.setRoom(rs.get(room.trim()));
                String timeslot = dataFormatter.formatCellValue(row.getCell(8));
                solution.setTimeSlot(ts.get(timeslot.trim()));
                solutions.add(solution);
            }

            presentations.sort(Comparator.comparing(PresentationDto::getTitle));
            presentations = presentations.stream().filter(distinctByKey(PresentationDto::getName)).collect(Collectors.toList());

            planning.setSolutions(solutions);
            planning.setRooms(new ArrayList<>(rs.values()));
            planning.setTimeslots(new ArrayList<>(ts.values()));
            planning.setStatus(StatusEnum.SOLUTION);

            SolutionChecker solutionChecker = new SolutionChecker();
            solutionChecker.generateStats(planning,
                    ls.values().stream().map(l -> (L) l).collect(Collectors.toList()),
                    presentations.stream().map(p -> (P) p).collect(Collectors.toList()),
                    ts.values().stream().map(t -> (T) t).collect(Collectors.toList()),
                    rs.values().stream().map(r -> (R) r).collect(Collectors.toList()));
            planning.setCost(solutionChecker.getTotalPlanningCost());

            log.info(planning.getPlanningStats());
            log.info();
            log.info("Planning Nr:    " + planning.getNr());
            log.info(planning.getPlanningAsTable());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
