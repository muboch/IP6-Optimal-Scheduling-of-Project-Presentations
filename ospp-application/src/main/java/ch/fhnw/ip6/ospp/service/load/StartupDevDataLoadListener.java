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
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Profile("dev")
@Log4j2
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

    private final Environment environment;

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (!Arrays.asList(environment.getActiveProfiles()).contains("dev")) {
            log.info("No dev dataload cause profile is not 'dev'");
            return;
        }

        ClassPathResource classPathResource = new ClassPathResource("devdata/lecturers.xlsx");
        MultipartFile lecturersMultipartFile = new MockMultipartFile("lecturers", "lecturers.xlsx", "text/plain", IOUtils.toByteArray(classPathResource.getInputStream()));
        Set<Lecturer> allLecturers = lecturerLoadService.loadLecturer(lecturersMultipartFile);
        allLecturers.forEach(lecturerService::save);
        log.info("Lecturers loaded ({})", allLecturers.size());

        classPathResource = new ClassPathResource("devdata/presentations.xlsx");
        MultipartFile presentationsMultipartFile = new MockMultipartFile("presentations", "presentations.xlsx", "text/plain", IOUtils.toByteArray(classPathResource.getInputStream()));
        Set<Presentation> allPresentations = presentationLoadService.loadPresentation(presentationsMultipartFile, allLecturers);
        allPresentations.forEach(presentationService::save);
        log.info("Presentations loaded ({})", allPresentations.size());

        classPathResource = new ClassPathResource("devdata/rooms.xlsx");
        MultipartFile roomsMultipartFile = new MockMultipartFile("rooms", "rooms.xlsx", "text/plain", IOUtils.toByteArray(classPathResource.getInputStream()));
        Set<Room> allRooms = roomLoadService.loadRooms(roomsMultipartFile);
        allRooms.forEach(roomService::save);
        log.info("Rooms loaded ({})", allRooms.size());

        classPathResource = new ClassPathResource("devdata/timeslots.xlsx");
        MultipartFile timeslotsMultipartFile = new MockMultipartFile("timeslots", "timeslots.xlsx", "text/plain", IOUtils.toByteArray(classPathResource.getInputStream()));
        AtomicInteger counter = new AtomicInteger();
        Set<Timeslot> allTimeslots = timeslotLoadService.loadTimeslots(timeslotsMultipartFile);
        allTimeslots
                .stream()
                .sorted(Timeslot.TIMESLOT_COMPARATOR)
                .forEach(timeslot -> {
                    timeslot.setSortOrder(counter.incrementAndGet());
                });

        allTimeslots.forEach(timeslotService::save);
        log.info("Timeslots loaded ({})", allTimeslots.size());

        classPathResource = new ClassPathResource("devdata/offtimes.xlsx");
        MultipartFile offtimesMultipartFile = new MockMultipartFile("offtimes", "offtimes.xlsx", "text/plain", IOUtils.toByteArray(classPathResource.getInputStream()));
        Set<Lecturer> offtimesLecturers = timeslotLoadService.loadOfftimes(offtimesMultipartFile, allLecturers, allTimeslots);
        offtimesLecturers.forEach(lecturerService::save);
        log.info("Offtimes loaded ({})", allTimeslots.size());

    }
}