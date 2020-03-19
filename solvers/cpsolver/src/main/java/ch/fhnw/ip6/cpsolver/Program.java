package ch.fhnw.ip6.cpsolver;

import ch.fhnw.ip6.common.classes.Lecturer;
import ch.fhnw.ip6.common.classes.Presentation;
import ch.fhnw.ip6.common.classes.Room;
import ch.fhnw.ip6.common.classes.Timeslot;
import ch.fhnw.ip6.common.util.JsonUtil;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;
import org.apache.commons.lang3.time.StopWatch;

import java.util.List;
import java.util.stream.Collectors;

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
        CpModel model = new CpModel();

        for (Presentation p : presentations) {
            p.setCoach(teachers.stream().filter(t -> t.getInitials().equals(p.getCoachInitials())).findFirst().get()); // Assign Coaches to Presentation
            p.setExpert(teachers.stream().filter(t -> t.getInitials().equals(p.getExpertInitials())).findFirst().get()); // Assign Experts to Presentation
        }

        //Create model. presTimeRoom[p,t,r] -> Presentation p happens in room r at time t
        IntVar[][][] presRoomTime = new IntVar[presentations.size()][rooms.size()][timeslots.size()];
        for (Timeslot t : timeslots) {
            for (Room r : rooms) {
                for (Presentation p : presentations) {
                    if (p.getType() != r.getType()) {
                        continue;
                    }
                    presRoomTime[p.getId()][r.getId()][t.getId()] = model.newBoolVar("presRoomTime_p" + p.getId() + "_r" + r.getId() + "_t" + t.getId());
                }
            }
        }

        System.out.println("Setup completed");
        // For each presentations, list the presentations that are not allowed to overlap
        List<Presentation>[] nonOverlappingPresentations = new List[teachers.size()];
        for (Lecturer l:teachers)
        {
            nonOverlappingPresentations[l.getId()] = presentations.stream().filter(ps -> ps.getExpert().getId() == l.getId() || ps.getCoach().getId() ==l.getId()).collect(Collectors.toList());
        }
        System.out.println("Overlap calculation completed");

        


        stopWatch.stop();
    }
}
