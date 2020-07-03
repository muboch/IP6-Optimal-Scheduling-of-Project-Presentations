package ch.fhnw.ip6.ospp.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresentationVO {

    private Long id;

    private Long room;

    private String type;

    private String title;

    private StudentVO studentOne;

    private StudentVO studentTwo;

    private LecturerVO coach;

    private LecturerVO expert;

    private Long timeslot;

}
