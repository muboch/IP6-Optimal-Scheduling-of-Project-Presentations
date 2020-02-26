package ch.fhnw.ip6.ospp;

import ch.fhnw.ip6.ospp.model.Discipline;
import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Room;
import ch.fhnw.ip6.ospp.model.RoomType;
import ch.fhnw.ip6.ospp.model.Student;
import ch.fhnw.ip6.ospp.model.Teacher;
import ch.fhnw.ip6.ospp.model.Timeslot;
import ch.fhnw.ip6.ospp.persistence.PresentationRepository;
import ch.fhnw.ip6.ospp.persistence.RoomRepository;
import ch.fhnw.ip6.ospp.persistence.StudentRepository;
import ch.fhnw.ip6.ospp.persistence.TeacherRepository;
import ch.fhnw.ip6.ospp.persistence.TimeslotRepository;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
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

        // Create Teachers
        createTeachers();

        // Create Students
        createStudents();

        // Create Rooms
        createRooms();

        // Create Timeslots
        createTimeslots();

        log.info("\uD83C\uDF08\uD83C\uDF08\uD83C\uDF08 Test Data created \uD83C\uDF08\uD83C\uDF08\uD83C\uDF08");
    }

    private void createTeachers() {

        Faker faker = new Faker();
        AtomicInteger idCounter = new AtomicInteger();

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
    }

    private void createRooms() {
        AtomicInteger idCounter = new AtomicInteger();
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
    }

    private void createTimeslots() {
        log.info("⌛️ Create Timeslots");

        AtomicInteger idCounter = new AtomicInteger();

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
    }

    private void createStudents() {

        final List<Teacher> teachers = teacherRepository.findAll();
        final Faker faker = new Faker();
        AtomicInteger idCounter = new AtomicInteger();

        final List<Discipline> disciplines = List.of(Discipline.values());
        final List<String> schoolClasses = List.of("3a", "3b", "3c");

        int numOfStuds = 10;

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
                    .title(faker.company().catchPhrase())
                    .build();

            presentation = presentationRepository.save(presentation);
            log.info(presentation.toString());
            numOfStuds--;
        }
    }
}
