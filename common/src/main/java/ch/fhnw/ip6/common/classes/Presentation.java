package ch.fhnw.ip6.common.classes;

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
    private String name;
    private String schoolclass;
    private String name2;
    private String schoolclass2;
    private String title;
    private Lecturer coach;
    private Lecturer expert;
    private String coachInitials;
    private String expertInitials;
    private String type;

}
