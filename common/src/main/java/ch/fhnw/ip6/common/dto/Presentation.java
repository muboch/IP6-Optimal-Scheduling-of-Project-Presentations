package ch.fhnw.ip6.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Presentation {

    private int id;
    private String nr;
    private Student studentOne;
    private Student studentTwo;
    private String title;
    private Lecturer coach;
    private Lecturer expert;
    private String type;

    public String getCoachInitials() {
        return coach.getInitials();
    }

    public String getExpertInitials() {
        return expert.getInitials();
    }

}
