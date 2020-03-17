package ch.fhnw.ip6.cpsolver;

import ch.fhnw.ip6.common.classes.Lecturer;
import ch.fhnw.ip6.common.classes.Presentation;
import ch.fhnw.ip6.common.classes.Room;
import ch.fhnw.ip6.common.classes.Timeslot;
import ch.fhnw.ip6.common.util.JsonUtil;

import java.util.List;

public class Program {

    public static void main(String[] args) {

        JsonUtil util = new JsonUtil();

        List<Presentation> presentations = util.getJsonAsList("presentations.json", Presentation.class);
        List<Lecturer> teachers = util.getJsonAsList("lecturers.json", Lecturer.class);
        List<Room> rooms = util.getJsonAsList("rooms.json", Room.class);
        List<Timeslot> timeslots = util.getJsonAsList("timeslots.json", Timeslot.class);




    }
}
