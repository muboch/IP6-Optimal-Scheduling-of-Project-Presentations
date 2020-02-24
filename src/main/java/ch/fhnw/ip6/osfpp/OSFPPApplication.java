package ch.fhnw.ip6.osfpp;

import ch.fhnw.ip6.osfpp.model.Presentation;
import ch.fhnw.ip6.osfpp.model.Student;
import ch.fhnw.ip6.osfpp.persistence.PresentationRepository;
import ch.fhnw.ip6.osfpp.persistence.StudentRepository;
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

import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class OSFPPApplication implements CommandLineRunner {


    private AtomicInteger idCounter = new AtomicInteger();

    @Autowired
    private PresentationRepository presentationRepository;
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    ResourceLoader resourceLoader;

    public static void main(String[] args) {
        SpringApplication.run(OSFPPApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {


        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try {
            Object obj = jsonParser.parse(new FileReader(loadFile().getFile()));

            JSONArray presentations = (JSONArray) obj;
            System.out.println(presentations);

            presentations.forEach(p -> parsePresentation((JSONObject) p));

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }

    }

    private void parsePresentation(JSONObject p) {
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
    }

    public Resource loadFile() {
        return resourceLoader.getResource("classpath:presentations.json");
    }
}
