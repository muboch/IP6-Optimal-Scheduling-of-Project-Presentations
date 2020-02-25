package ch.fhnw.ip6.osfpp;

import ch.fhnw.ip6.osfpp.model.Presentation;
import ch.fhnw.ip6.osfpp.model.Room;
import ch.fhnw.ip6.osfpp.model.RoomType;
import ch.fhnw.ip6.osfpp.model.Student;
import ch.fhnw.ip6.osfpp.persistence.PresentationRepository;
import ch.fhnw.ip6.osfpp.persistence.RoomRepository;
import ch.fhnw.ip6.osfpp.persistence.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
@RequiredArgsConstructor
public class OSFPPApplication implements CommandLineRunner {


    private AtomicInteger idCounter = new AtomicInteger();

    private final PresentationRepository presentationRepository;
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final ResourceLoader resourceLoader;

    public static void main(String[] args) {
        SpringApplication.run(OSFPPApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        //JSON parser object to parse read file

        File presentations = resourceLoader.getResource("classpath:presentations.json").getFile();
        loadPresentation(presentations);
        File rooms = resourceLoader.getResource("classpath:rooms.json").getFile();
        loadRooms(rooms);

    }

    private void loadRooms(File file) {
        JSONParser jsonParser = new JSONParser();

        try {
            Object obj = jsonParser.parse(new FileReader(file));

            JSONArray rooms = (JSONArray) obj;

            rooms.forEach(a -> {
                JSONObject p = (JSONObject) a;
                Room room = new Room();
                room.setId((long) p.get("Id"));
                room.setRoomNumber((String) p.get("name"));
                if (Boolean.parseBoolean((String) p.get("musicroom"))) {
                    room.setRoomType(RoomType.MUSIC);
                } else if (Boolean.parseBoolean((String) p.get("reserve"))) {
                    room.setRoomType(RoomType.RESERVE);
                } else if (Boolean.parseBoolean((String) p.get("artroom"))) {
                    room.setRoomType(RoomType.ART);
                } else if (Boolean.parseBoolean((String) p.get("danceroom"))) {
                    room.setRoomType(RoomType.DANCE);
                } else {
                    room.setRoomType(RoomType.NORMAL);
                }
                roomRepository.save(room);
            });
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPresentation(File file) {
        JSONParser jsonParser = new JSONParser();

        try {
            Object obj = jsonParser.parse(new FileReader(file));

            JSONArray presentations = (JSONArray) obj;

            presentations.forEach(a -> {
                JSONObject p = (JSONObject) a;
                Presentation presentation = new Presentation();
                presentation.setId((long) p.get("id"));
                presentation.setTitle((String) p.get("Titel"));

                Student student = new Student();
                String name = (String) p.get("Name");
                student.setFirstname(name.split(" ")[0]);
                student.setLastname(name.split(" ")[1]);
                student.setId(idCounter.getAndAdd(1));
                studentRepository.save(student);
                presentation.setStudent(student);
                presentationRepository.save(presentation);
            });
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }
}
