package ch.fhnw.ip6.ospp.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PresentationVO {

    private Long id;

    private Long room;

    private String nr;

    private String type;

    private String title;

    private Long studentOne;

    private Long studentTwo;

    private Long coach;

    private Long expert;

    private Long timeslot;

}
