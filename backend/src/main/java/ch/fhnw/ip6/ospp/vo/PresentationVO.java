package ch.fhnw.ip6.ospp.vo;

public interface PresentationVO {

    int getId();

    RoomVO getRoom();

    String getTitle();

    StudentVO getFirstStudent();

    StudentVO getSecondStudent();

    TeacherVO getCoach();

    TeacherVO getExpert();

    TimeslotVO getTimeslot();

}
