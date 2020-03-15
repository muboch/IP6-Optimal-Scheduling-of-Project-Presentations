package ch.fhnw.ip6.ospp.vo;

public interface PresentationVO {

    long getId();

    RoomVO getRoom();

    String getTitle();

    StudentVO getStudent();

    TeacherVO getExaminator();

    TeacherVO getExpert();

    TimeslotVO getTimeslot();

}
