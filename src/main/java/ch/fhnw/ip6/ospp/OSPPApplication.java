package ch.fhnw.ip6.ospp;

import ch.fhnw.ip6.ospp.model.*;
import ch.fhnw.ip6.ospp.persistence.*;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class OSPPApplication implements CommandLineRunner {


    static {
        System.loadLibrary("jniortools");
    }

    private final PresentationRepository presentationRepository;
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final TeacherRepository teacherRepository;
    private final TimeslotRepository timeslotRepository;

    public static void main(String[] args) {
        SpringApplication.run(OSPPApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        Faker faker = new Faker();

        AtomicInteger idCounter = new AtomicInteger();

        // Create Teachers
        log.info("\uD83D\uDC68\uD83C\uDFFC\u200D\uD83C\uDFEB Create Teachers");
        int numOfTeachers = 20;

        while (numOfTeachers > 0) {
            String firstname = faker.name().firstName();
            String lastname = faker.name().lastName();

            Teacher teacher = Teacher.teacherBuilder()
                    .id(idCounter.getAndAdd(1))
                    .initials(firstname.toLowerCase().substring(0, 2) + lastname.toLowerCase().substring(0, 2))
                    .firstname(faker.name().firstName())
                    .lastname(faker.name().lastName())
                    .email((firstname + "." + lastname + "@edubs.ch").toLowerCase())
                    .build();
            teacher = teacherRepository.save(teacher);
            log.info(teacher.toString());
            numOfTeachers--;
        }

        List<Teacher> teachers = teacherRepository.findAll();


        // Create Students

        List<Discipline> disciplines = List.of(Discipline.values());
        List<String> schoolClasses = List.of("3a", "3b", "3c");

        int numOfStuds = 100;

        log.info("\uD83D\uDE4B\uD83C\uDFFC\u200D Create Students and Presentations");
        while (numOfStuds > 0) {

            String firstname = faker.name().firstName();
            String lastname = faker.name().lastName();
            Student student = Student.studentBuilder()
                    .id(idCounter.getAndAdd(1))
                    .firstname(firstname)
                    .lastname(lastname)
                    .discipline(disciplines.get(faker.random().nextInt(0, disciplines.size() - 1)))
                    .email((firstname + "." + lastname + "@edubs.ch").toLowerCase())
                    .schoolClass(schoolClasses.get(faker.random().nextInt(0, 2)))
                    .build();
            student = studentRepository.save(student);

            log.info(student.toString());

            Teacher examinator = teachers.get(faker.random().nextInt(0, teachers.size() - 1));
            Teacher expert = examinator;
            while (examinator == expert) {
                expert = teachers.get(faker.random().nextInt(0, teachers.size() - 1));
            }

            Presentation presentation = Presentation.builder()
                    .id(idCounter.getAndAdd(1))
                    .student(student)
                    .examinator(examinator)
                    .expert(expert)
                    .title(faker.book().title())
                    .build();

            presentation = presentationRepository.save(presentation);
            log.info(presentation.toString());
            numOfStuds--;
        }

        // Create Rooms
        log.info("\uD83C\uDFE0 Create Rooms");
        EnumMap<RoomType, Integer> numberOfRooms = new EnumMap<>(RoomType.class);
        numberOfRooms.put(RoomType.AULA, 1);
        numberOfRooms.put(RoomType.ART, 4);
        numberOfRooms.put(RoomType.MUSIC, 4);
        numberOfRooms.put(RoomType.DANCE, 2);
        numberOfRooms.put(RoomType.NORMAL, 20);
        numberOfRooms.put(RoomType.RESERVE, 7);

        numberOfRooms.forEach((type, value) -> {
            int cntr = value;
            while (cntr > 0) {
                Room room = Room.builder()
                        .id(idCounter.getAndAdd(1))
                        .roomNumber(type.name().substring(0, 1) + String.format("" + cntr, "%02d"))
                        .roomType(type)
                        .build();
                room = roomRepository.save(room);
                log.info(room.toString());
                cntr--;
            }
        });

        // Create Timeslots
        log.info("⌛️ Create Timeslots");
        int slotSize = 45;
        int slotsPerDay = 13;
        int numOfDays = 2;
        final LocalDateTime begin = LocalDateTime.of(2020, 1, 1, 8, 0);
        LocalDateTime prevTime = begin;
        while (numOfDays > 0) {
            for (int i = 0; i < slotsPerDay; i++) {
                LocalDateTime from = prevTime;
                LocalDateTime to = from.plusMinutes(slotSize);
                Timeslot slot = Timeslot.builder()
                        .id(idCounter.getAndAdd(1))
                        .begin(from)
                        .end(to)
                        .build();
                slot = timeslotRepository.save(slot);
                log.info(slot.toString());
                prevTime = to;
            }
            prevTime = begin.plusDays(1);
            numOfDays--;
        }
        log.info("\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08 Test Data created \uD83C\uDF08\uD83C\uDF08\uD83C\uDF08");
    }
}
