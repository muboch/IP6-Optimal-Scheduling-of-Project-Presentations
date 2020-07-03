package ch.fhnw.ip6.solutionchecker;


import ch.fhnw.ip6.common.dto.LecturerDto;
import ch.fhnw.ip6.common.dto.PresentationDto;
import ch.fhnw.ip6.common.dto.RoomDto;
import ch.fhnw.ip6.common.dto.Solution;
import ch.fhnw.ip6.common.dto.TimeslotDto;
import ch.fhnw.ip6.common.dto.marker.L;
import ch.fhnw.ip6.common.dto.marker.P;
import ch.fhnw.ip6.common.dto.marker.R;
import ch.fhnw.ip6.common.dto.marker.T;
import ch.fhnw.ip6.common.util.CostUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SolutionCheckerTest {

    @Test
    public void getUsedTimeslots() {

        List<R> rooms = getRooms(5);
        Map<String, L> lecturers = getLecturers(4);
        List<P> presentations = getPresentations(10, lecturers);
        List<T> timeslotes = getTimeslots(5);

        Solution solution1 = new Solution();
        solution1.setTimeSlot(timeslotes.get(0));
        solution1.setRoom(rooms.get(0));
        solution1.setPresentation(presentations.get(0));
        solution1.setExpert(lecturers.get(presentations.get(0).getExpertInitials()));
        solution1.setCoach(lecturers.get(presentations.get(0).getCoachInitials()));

        Solution solution2 = new Solution();
        solution2.setTimeSlot(timeslotes.get(1));
        solution2.setRoom(rooms.get(1));
        solution2.setPresentation(presentations.get(1));
        solution2.setExpert(lecturers.get(presentations.get(1).getExpertInitials()));
        solution2.setCoach(lecturers.get(presentations.get(1).getCoachInitials()));
        Set<Solution> solutions = new HashSet<>();
        solutions.add(solution1);
        solutions.add(solution2);

        SolutionChecker solutionChecker = new SolutionChecker();
        int usedTimeslots = solutionChecker.getUsedTimeslots(solutions, timeslotes);
        Assert.assertEquals(2, usedTimeslots);
        Assert.assertEquals(20, solutionChecker.getTotalUsedTimeslotsCosts());
    }

    @Test
    public void getUsedRooms() {

        List<R> rooms = getRooms(5);
        Map<String, L> lecturers = getLecturers(4);
        List<P> presentations = getPresentations(10, lecturers);
        List<T> timeslotes = getTimeslots(5);

        Solution solution1 = new Solution();
        solution1.setTimeSlot(timeslotes.get(0));
        solution1.setRoom(rooms.get(0));
        solution1.setPresentation(presentations.get(0));
        solution1.setExpert(lecturers.get(presentations.get(0).getExpertInitials()));
        solution1.setCoach(lecturers.get(presentations.get(0).getCoachInitials()));

        Solution solution2 = new Solution();
        solution2.setTimeSlot(timeslotes.get(1));
        solution2.setRoom(rooms.get(1));
        solution2.setPresentation(presentations.get(1));
        solution2.setExpert(lecturers.get(presentations.get(1).getExpertInitials()));
        solution2.setCoach(lecturers.get(presentations.get(1).getCoachInitials()));
        Set<Solution> solutions = new HashSet<>();
        solutions.add(solution1);
        solutions.add(solution2);

        SolutionChecker solutionChecker = new SolutionChecker();
        int usedRooms = solutionChecker.getUsedRooms(solutions, rooms);
        Assert.assertEquals(2, usedRooms);
        Assert.assertEquals(2 * CostUtil.USED_ROOM_COST, solutionChecker.getTotalUsedRoomsCosts());

    }

    @Test
    public void getRoomSwitches() {

        List<R> rooms = getRooms(5);
        Map<String, L> lecturers = getLecturers(5);
        List<L> lecs = new ArrayList<>(lecturers.values());
        List<T> timeslotes = getTimeslots(5);

        List<P> presentations = new ArrayList<>();

        PresentationDto p1 = PresentationDto.builder().expert((LecturerDto) lecs.get(0)).expertInitials(lecs.get(0).getInitials()).coach((LecturerDto) lecs.get(1)).coachInitials(lecs.get(1).getInitials()).build();
        PresentationDto p2 = PresentationDto.builder().expert((LecturerDto) lecs.get(0)).expertInitials(lecs.get(0).getInitials()).coach((LecturerDto) lecs.get(1)).coachInitials(lecs.get(1).getInitials()).build();
        PresentationDto p3 = PresentationDto.builder().expert((LecturerDto) lecs.get(0)).expertInitials(lecs.get(0).getInitials()).coach((LecturerDto) lecs.get(2)).coachInitials(lecs.get(2).getInitials()).build();
        PresentationDto p4 = PresentationDto.builder().expert((LecturerDto) lecs.get(2)).expertInitials(lecs.get(2).getInitials()).coach((LecturerDto) lecs.get(3)).coachInitials(lecs.get(3).getInitials()).build();

        PresentationDto p5 =  PresentationDto.builder().expert((LecturerDto) lecs.get(4)).expertInitials(lecs.get(4).getInitials()).coach((LecturerDto) lecs.get(3)).coachInitials(lecs.get(3).getInitials()).build();
        PresentationDto p6 =  PresentationDto.builder().expert((LecturerDto) lecs.get(4)).expertInitials(lecs.get(4).getInitials()).coach((LecturerDto) lecs.get(3)).coachInitials(lecs.get(3).getInitials()).build();

        presentations.add(p1);
        presentations.add(p2);
        presentations.add(p3);
        presentations.add(p4);
        presentations.add(p5);
        presentations.add(p6);


        Solution solution1 = new Solution();
        solution1.setTimeSlot(timeslotes.get(0));
        solution1.setRoom(rooms.get(0));
        solution1.setPresentation(p1);
        solution1.setExpert(lecturers.get(p1.getExpertInitials()));
        solution1.setCoach(lecturers.get(p1.getCoachInitials()));

        Solution solution2 = new Solution();
        solution2.setTimeSlot(timeslotes.get(1));
        solution2.setRoom(rooms.get(0));
        solution2.setPresentation(p2);
        solution2.setExpert(lecturers.get(p2.getExpertInitials()));
        solution2.setCoach(lecturers.get(p2.getCoachInitials()));

        Solution solution3 = new Solution();
        solution3.setTimeSlot(timeslotes.get(2));
        solution3.setRoom(rooms.get(1));
        solution3.setPresentation(p3);
        solution3.setExpert(lecturers.get(p3.getExpertInitials()));
        solution3.setCoach(lecturers.get(p3.getCoachInitials()));

        Solution solution4 = new Solution();
        solution4.setTimeSlot(timeslotes.get(1));
        solution4.setRoom(rooms.get(3));
        solution4.setPresentation(p4);
        solution4.setExpert(lecturers.get(p4.getExpertInitials()));
        solution4.setCoach(lecturers.get(p4.getCoachInitials()));

        Solution solution5 = new Solution();
        solution5.setTimeSlot(timeslotes.get(0));
        solution5.setRoom(rooms.get(3));
        solution5.setPresentation(p5);
        solution5.setExpert(lecturers.get(p5.getExpertInitials()));
        solution5.setCoach(lecturers.get(p5.getCoachInitials()));

        Solution solution6 = new Solution();
        solution6.setTimeSlot(timeslotes.get(2));
        solution6.setRoom(rooms.get(3));
        solution6.setPresentation(p6);
        solution6.setExpert(lecturers.get(p6.getExpertInitials()));
        solution6.setCoach(lecturers.get(p6.getCoachInitials()));

        Set<Solution> solutions = new HashSet<>();
        solutions.add(solution1);
        solutions.add(solution2);
        solutions.add(solution3);
        solutions.add(solution4);

        Set<Solution> oneFreeTimeslotSolutions = new HashSet<>();

        oneFreeTimeslotSolutions.add(solution5);
        oneFreeTimeslotSolutions.add(solution6);

        SolutionChecker solutionChecker = new SolutionChecker();
        int roomSwitches = solutionChecker.getRoomSwitches(solutions, lecs, timeslotes, presentations);
        Assert.assertEquals(2, roomSwitches);
        Assert.assertEquals(2 * CostUtil.ROOM_SWITCH_COST, solutionChecker.getTotalRoomSwitchesCosts());

        int rSOneFreeTimeslot = solutionChecker.getRoomSwitches(oneFreeTimeslotSolutions, lecs, timeslotes, presentations);
        Assert.assertEquals(2, rSOneFreeTimeslot);


    }

        @Test
        public void checkEachPresentationOnce() {

        List<R> rooms = getRooms(5);
        Map<String, L> lecturers = getLecturers(4);
        List<L> lecs = new ArrayList<>(lecturers.values());
        List<T> timeslotes = getTimeslots(5);

        List<P> presentations = new ArrayList<>();

        PresentationDto p1 = PresentationDto.builder().id(1).expertInitials(lecs.get(0).getInitials()).coachInitials(lecs.get(1).getInitials()).build();
        PresentationDto p2 = PresentationDto.builder().id(2).expertInitials(lecs.get(0).getInitials()).coachInitials(lecs.get(1).getInitials()).build();
        presentations.add(p1);
        presentations.add(p2);

        Solution solution1 = new Solution();
        solution1.setTimeSlot(timeslotes.get(0));
        solution1.setRoom(rooms.get(0));
        solution1.setPresentation(p1);
        solution1.setExpert(lecturers.get(p1.getExpertInitials()));
        solution1.setCoach(lecturers.get(p1.getCoachInitials()));

        Solution solution2 = new Solution();
        solution2.setTimeSlot(timeslotes.get(1));
        solution2.setRoom(rooms.get(0));
        solution2.setPresentation(p2);
        solution2.setExpert(lecturers.get(p2.getExpertInitials()));
        solution2.setCoach(lecturers.get(p2.getCoachInitials()));

        Set<Solution> solutions = new HashSet<>();
        solutions.add(solution1);
        solutions.add(solution2);

        SolutionChecker solutionChecker = new SolutionChecker();
        boolean eachPresentationOnce = solutionChecker.checkEachPresentationOnce(solutions, presentations);
        Assert.assertTrue(eachPresentationOnce);

        solution2.setPresentation(p1);
        eachPresentationOnce = solutionChecker.checkEachPresentationOnce(solutions, presentations);
        Assert.assertFalse(eachPresentationOnce);

    }

    @Test
    public void checkOnePresentationPerTimeslotForLecturer() {

        List<R> rooms = getRooms(5);
        Map<String, L> lecturers = getLecturers(4);
        List<L> lecs = new ArrayList<>(lecturers.values());
        List<T> timeslots = getTimeslots(5);

        List<P> presentations = new ArrayList<>();

        PresentationDto p1 = PresentationDto.builder().expertInitials(lecs.get(0).getInitials()).coachInitials(lecs.get(1).getInitials()).build();
        PresentationDto p2 = PresentationDto.builder().expertInitials(lecs.get(0).getInitials()).coachInitials(lecs.get(1).getInitials()).build();
        presentations.add(p1);
        presentations.add(p2);

        Solution solution1 = new Solution();
        solution1.setTimeSlot(timeslots.get(0));
        solution1.setRoom(rooms.get(0));
        solution1.setPresentation(p1);
        solution1.setExpert(lecturers.get(p1.getExpertInitials()));
        solution1.setCoach(lecturers.get(p1.getCoachInitials()));

        Solution solution2 = new Solution();
        solution2.setTimeSlot(timeslots.get(0));
        solution2.setRoom(rooms.get(1));
        solution2.setPresentation(p2);
        solution2.setExpert(lecturers.get(p2.getExpertInitials()));
        solution2.setCoach(lecturers.get(p2.getCoachInitials()));

        Set<Solution> solutions = new HashSet<>();
        solutions.add(solution1);
        solutions.add(solution2);

        SolutionChecker solutionChecker = new SolutionChecker();
        boolean onePresentationPerTimeslotForLecturer = solutionChecker.checkOnePresentationPerTimeslotForLecturer(solutions, timeslots, lecs);
        Assert.assertFalse(onePresentationPerTimeslotForLecturer);

        solution2.setTimeSlot(timeslots.get(1));
        onePresentationPerTimeslotForLecturer = solutionChecker.checkOnePresentationPerTimeslotForLecturer(solutions, timeslots, lecs);
        Assert.assertTrue(onePresentationPerTimeslotForLecturer);


    }

    @Test
    public void checkRoomUsedMaxOncePerTime() {
        List<R> rooms = getRooms(5);
        Map<String, L> lecturers = getLecturers(4);
        List<L> lecs = new ArrayList<>(lecturers.values());
        List<T> timeslots = getTimeslots(5);

        List<P> presentations = new ArrayList<>();

        PresentationDto p1 = PresentationDto.builder().expertInitials(lecs.get(0).getInitials()).coachInitials(lecs.get(1).getInitials()).build();
        PresentationDto p2 = PresentationDto.builder().expertInitials(lecs.get(0).getInitials()).coachInitials(lecs.get(1).getInitials()).build();
        presentations.add(p1);
        presentations.add(p2);

        Solution solution1 = new Solution();
        solution1.setTimeSlot(timeslots.get(0));
        solution1.setRoom(rooms.get(0));
        solution1.setPresentation(p1);
        solution1.setExpert(lecturers.get(p1.getExpertInitials()));
        solution1.setCoach(lecturers.get(p1.getCoachInitials()));

        Solution solution2 = new Solution();
        solution2.setTimeSlot(timeslots.get(1));
        solution2.setRoom(rooms.get(0));
        solution2.setPresentation(p2);
        solution2.setExpert(lecturers.get(p2.getExpertInitials()));
        solution2.setCoach(lecturers.get(p2.getCoachInitials()));

        Set<Solution> solutions = new HashSet<>();
        solutions.add(solution1);
        solutions.add(solution2);

        SolutionChecker solutionChecker = new SolutionChecker();
        boolean roomUsedMaxOncePerTime = solutionChecker.checkRoomUsedMaxOncePerTime(solutions, rooms, timeslots);
        Assert.assertTrue(roomUsedMaxOncePerTime);

        solution2.setTimeSlot(solution1.getTimeSlot());
        solution2.setRoom(solution1.getRoom());
        roomUsedMaxOncePerTime = solutionChecker.checkRoomUsedMaxOncePerTime(solutions, rooms, timeslots);
        Assert.assertFalse(roomUsedMaxOncePerTime);
    }

    private List<R> getRooms(int amount) {
        List<R> rooms = new ArrayList<>();
        while (amount > 0) {
            rooms.add(RoomDto.builder().name("R" + amount).id(amount).place("place").reserve(false).build());
            amount--;
        }
        return rooms;
    }

    private List<T> getTimeslots(int amount) {
        List<T> timeslots = new ArrayList<>();
        int sortOrder = 0;
        while (amount > 0) {
            timeslots.add(TimeslotDto.builder().date("Fr " + amount).id(amount).block(1).priority(10).sortOrder(sortOrder).build());
            amount--;
            sortOrder++;
        }
        return timeslots;
    }

    private Map<String, L> getLecturers(int amount) {
        List<L> lecturers = new ArrayList<>();
        while (amount > 0) {
            lecturers.add(LecturerDto.builder().firstname("First" + amount).lastname("Last" + amount).email("email" + amount + "@edubs.ch").initials("ini" + amount).id(amount).build());
            amount--;
        }
        return lecturers.stream().collect(Collectors.toMap(L::getInitials, l -> l));
    }

    private List<P> getPresentations(int amount, Map<String, L> lecturers) {
        List<L> lecs = new ArrayList<>(lecturers.values());
        List<P> presentations = new ArrayList<>();
        while (amount > 0) {
            presentations.add(PresentationDto.builder()
                    .id(amount).title("title" + amount).name("student" + amount)
                    .coachInitials(lecs.get(amount % lecs.size()).getInitials())
                    .expertInitials(lecs.get(amount % lecs.size()).getInitials())
                    .build());
            amount--;
        }
        return presentations;
    }
}