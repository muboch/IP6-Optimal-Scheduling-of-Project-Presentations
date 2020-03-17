package ch.fhnw.ip6.orcpsolver;

import ch.fhnw.ip6.orcpsolver.classes.Coach;
import ch.fhnw.ip6.orcpsolver.classes.Presentation;
import ch.fhnw.ip6.orcpsolver.classes.Room;
import ch.fhnw.ip6.orcpsolver.classes.TimeSlot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Program {

    public static void main(String[] args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ObjectMapper mapper = new ObjectMapper();


        // 1. JSON file to Java object
        try {
            List<Room> rooms = Arrays.asList(mapper.readValue( Resources.toString(Resources.getResource("rooms.json"), StandardCharsets.UTF_8) , Room[].class));

            List<Coach> coaches = Arrays.asList(mapper.readValue( Resources.toString(Resources.getResource("profs.json"), StandardCharsets.UTF_8) , Coach[].class));
            List<Presentation> presentations = Arrays.asList(mapper.readValue( Resources.toString(Resources.getResource("presentations.json"), StandardCharsets.UTF_8) , Presentation[].class));
            List<TimeSlot> timeslots = Arrays.asList(mapper.readValue( Resources.toString(Resources.getResource("timeslots.json"), StandardCharsets.UTF_8) , TimeSlot[].class));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Creates the model.
        //var model = new CpModel();
    }
}