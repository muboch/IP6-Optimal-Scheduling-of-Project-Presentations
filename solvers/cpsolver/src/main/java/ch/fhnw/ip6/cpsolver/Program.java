package ch.fhnw.ip6.cpsolver;

import ch.fhnw.ip6.common.classes.Lecturer;
import ch.fhnw.ip6.common.classes.Presentation;
import ch.fhnw.ip6.common.classes.Room;
import ch.fhnw.ip6.common.classes.Timeslot;
import ch.fhnw.ip6.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.List;

@Slf4j
public class Program {

    public static void main(String[] args) {

        JsonUtil util = new JsonUtil();

        List<Presentation> presentations = util.getJsonAsList("presentations.json", Presentation.class);
        List<Lecturer> teachers = util.getJsonAsList("lecturers.json", Lecturer.class);
        List<Room> rooms = util.getJsonAsList("rooms.json", Room.class);
        List<Timeslot> timeslots = util.getJsonAsList("timeslots.json", Timeslot.class);

        presentations.forEach(System.out::println);
        teachers.forEach(System.out::println);
        rooms.forEach(System.out::println);
        timeslots.forEach(System.out::println);


        StopWatch stopWatch = new StopWatch();
        stopWatch.start();



        stopWatch.stop();
        log.info("Time used to find the final solution: {}", stopWatch.getTime());
    }
}
