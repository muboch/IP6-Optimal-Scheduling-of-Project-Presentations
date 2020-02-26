package ch.fhnw.ip6.ospp.controller;

import ch.fhnw.ip6.ospp.model.Presentation;
import ch.fhnw.ip6.ospp.model.Room;
import ch.fhnw.ip6.ospp.model.Teacher;
import ch.fhnw.ip6.ospp.model.Timeslot;
import ch.fhnw.ip6.ospp.persistence.PresentationRepository;
import ch.fhnw.ip6.ospp.persistence.RoomRepository;
import ch.fhnw.ip6.ospp.persistence.StudentRepository;
import ch.fhnw.ip6.ospp.persistence.TeacherRepository;
import ch.fhnw.ip6.ospp.persistence.TimeslotRepository;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("solve")
@RequiredArgsConstructor
@Slf4j
public class SolveController {

    private final PresentationRepository presentationRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final TimeslotRepository timeslotRepository;
    private final RoomRepository roomRepository;

    @GetMapping
    public CpSolverStatus solve() {

        final List<Teacher> teachers = teacherRepository.findAll();
        final List<Presentation> presentations = presentationRepository.findAll();
        final List<Timeslot> timeslots = timeslotRepository.findAll();
        final List<Room> rooms = roomRepository.findAll();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // Creates the model.
        CpModel model = new CpModel();

        //Create model. presTimeRoom[p,t,r] -> Presentation p happens in room r at time t
        IntVar[][][][] presRoomTime = new IntVar[teachers.size()][presentations.size()][rooms.size()][timeslots.size()];

        for (int teacherIdx = 0; teacherIdx < teachers.size(); teacherIdx++) {
            for (int timeslotIdx = 0; timeslotIdx < timeslots.size(); timeslotIdx++) {
                for (int roomIdx = 0; roomIdx < rooms.size(); roomIdx++) {
                    for (int presentationIdx = 0; presentationIdx < presentations.size(); presentationIdx++) {
                        presRoomTime[teacherIdx][presentationIdx][roomIdx][timeslotIdx] = model.newBoolVar(String.format("presRoomTime_prof%s_p%s_r%s_t%s", teacherIdx, presentationIdx, roomIdx, timeslotIdx));
                    }
                }
            }
        }


        // For each Presentation, there must be 1 (room,timeslot,professor) pair.
        for (int presentationIdx = 0; presentationIdx < presentations.size(); presentationIdx++) {
            List<IntVar> temp = new ArrayList<>();

            for (int teacherIdx = 0; teacherIdx < teachers.size(); teacherIdx++) {
                for (int timeslotIdx = 0; timeslotIdx < timeslots.size(); timeslotIdx++) {
                    for (int roomIdx = 0; roomIdx < rooms.size(); roomIdx++) {
                        temp.add(presRoomTime[teacherIdx][presentationIdx][roomIdx][timeslotIdx]);
                    }
                }
            }

            model.addEquality(LinearExpr.sum(temp.toArray(IntVar[]::new)), 1);
        }

        // For each (professor, room, timeslot) pair there must be <=1 presentation -> Max 1 Presentation per Room/Time/Professor Slot
        for (int teacherIdx = 0; teacherIdx < teachers.size(); teacherIdx++) {
            for (int roomIdx = 0; roomIdx < rooms.size(); roomIdx++) {
                for (int timeslotIdx = 0; timeslotIdx < timeslots.size(); timeslotIdx++) {
                    IntVar[] temp = new IntVar[presentations.size()];
                    int cntr = 0;
                    for (int presentationIdx = 0; presentationIdx < presentations.size(); presentationIdx++) {
                        temp[cntr++] = presRoomTime[teacherIdx][presentationIdx][roomIdx][timeslotIdx];
                    }

                    model.addLessOrEqual(LinearExpr.sum(temp), 1);
                }
            }
        }

        // For each professor,timeslot pair, there must be max 1 (presentation,room) pair. --> Professors cant have more than one presentation at a time
        for (int teacherIdx = 0; teacherIdx < teachers.size(); teacherIdx++) {

            for (int timeslotIdx = 0; timeslotIdx < timeslots.size(); timeslotIdx++) {
                List<IntVar> temp = new ArrayList<>();

                for (int presentationIdx = 0; presentationIdx < presentations.size(); presentationIdx++) {
                    for (int roomIdx = 0; roomIdx < rooms.size(); roomIdx++) {
                        temp.add(presRoomTime[teacherIdx][presentationIdx][roomIdx][timeslotIdx]);
                    }
                }
                model.addLessOrEqual(LinearExpr.sum(temp.toArray(IntVar[]::new)), 1);
            }
        }

        CpSolver solver = new CpSolver();

        CpSolverStatus cpSolverStatus = solver.solve(model);
        stopWatch.stop();

        log.info("Duration: {}", stopWatch.getTime(TimeUnit.SECONDS));
        log.info("Solution Info: {}", solver.response().getSolutionInfo());
        log.info("Solutions found :{}", solver.response().getSolutionCount());
        log.info(solver.responseStats());
        return cpSolverStatus;

    }

}
