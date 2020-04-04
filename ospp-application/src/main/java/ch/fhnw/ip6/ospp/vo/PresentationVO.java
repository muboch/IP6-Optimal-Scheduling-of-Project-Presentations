package ch.fhnw.ip6.ospp.vo;

public interface PresentationVO {

    int getId();

    RoomVO getRoom();

    int getExternalId();

    String getType();

    String getTitle();

    StudentVO getStudentOne();

    StudentVO getStudentTwo();

    LecturerVO getCoach();

    LecturerVO getExpert();

    TimeslotVO getTimeslot();

}
