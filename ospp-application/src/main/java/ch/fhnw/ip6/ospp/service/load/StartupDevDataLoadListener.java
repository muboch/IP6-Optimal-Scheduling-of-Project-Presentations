package ch.fhnw.ip6.ospp.service.load;

import ch.fhnw.ip6.ospp.model.Lecturer;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Room;
import ch.fhnw.ip6.ospp.model.Timeslot;
import ch.fhnw.ip6.ospp.service.LecturerService;
import ch.fhnw.ip6.ospp.service.PresentationService;
import ch.fhnw.ip6.ospp.service.RoomService;
import ch.fhnw.ip6.ospp.service.TimeslotService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

@Profile("dev")
@Slf4j
@Component
@RequiredArgsConstructor
public class StartupDevDataLoadListener implements ApplicationListener<ContextRefreshedEvent> {

    private final PresentationLoadService presentationLoadService;
    private final RoomLoadService roomLoadService;
    private final LecturerLoadService lecturerLoadService;
    private final TimeslotLoadService timeslotLoadService;

    private final PresentationService presentationService;
    private final RoomService roomService;
    private final LecturerService lecturerService;
    private final TimeslotService timeslotService;

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        ClassPathResource classPathResource = new ClassPathResource("devdata/lecturers.xlsx");
        File lecturers = classPathResource.getFile();
        FileInputStream lecturersInput = new FileInputStream(lecturers);
        MultipartFile lecturersMultipartFile = new MockMultipartFile("lecturers", lecturers.getName(), "text/plain", IOUtils.toByteArray(lecturersInput));
        Set<Lecturer> allLecturers = lecturerLoadService.loadLecturer(lecturersMultipartFile);
        allLecturers.forEach(lecturerService::save);
        log.info("Lecturers loaded ({})", allLecturers.size());

        classPathResource = new ClassPathResource("devdata/presentations.xlsx");
        File presentations = classPathResource.getFile();
        FileInputStream presentationsInput = new FileInputStream(presentations);
        MultipartFile presentationsMultipartFile = new MockMultipartFile("presentations", presentations.getName(), "text/plain", IOUtils.toByteArray(presentationsInput));
        Set<Presentation> allPresentations = presentationLoadService.loadPresentation(presentationsMultipartFile, allLecturers);
        allPresentations.forEach(presentationService::save);
        log.info("Presentations loaded ({})", allPresentations.size());

        classPathResource = new ClassPathResource("devdata/rooms.xlsx");
        File rooms = classPathResource.getFile();
        FileInputStream roomsInput = new FileInputStream(rooms);
        MultipartFile roomsMultipartFile = new MockMultipartFile("rooms", rooms.getName(), "text/plain", IOUtils.toByteArray(roomsInput));
        Set<Room> allRooms = roomLoadService.loadRooms(roomsMultipartFile);
        log.info("Rooms loaded ({})", allRooms.size());

        classPathResource = new ClassPathResource("devdata/timeslots.xlsx");
        File timeslots = classPathResource.getFile();
        FileInputStream timeslotsInput = new FileInputStream(timeslots);
        MultipartFile timeslotsMultipartFile = new MockMultipartFile("timeslots", timeslots.getName(), "text/plain", IOUtils.toByteArray(timeslotsInput));
        Set<Timeslot> allTimeslots = timeslotLoadService.loadTimeslots(timeslotsMultipartFile);
        allTimeslots.forEach(timeslotService::save);
        log.info("Timeslots loaded ({})", allTimeslots.size());

        classPathResource = new ClassPathResource("devdata/offtimes.xlsx");
        File offtimes = classPathResource.getFile();
        FileInputStream offtimesInput = new FileInputStream(offtimes);
        MultipartFile offtimesMultipartFile = new MockMultipartFile("offtimes", offtimes.getName(), "text/plain", IOUtils.toByteArray(offtimesInput));
        Set<Lecturer> offtimesLecturers = timeslotLoadService.loadOfftimes(offtimesMultipartFile, allLecturers, allTimeslots);
        offtimesLecturers.forEach(lecturerService::save);
        log.info("Offtimes loaded ({})", allTimeslots.size());

    }
}