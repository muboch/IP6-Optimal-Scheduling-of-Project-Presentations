package ch.fhnw.ip6.ospp.model;


import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class Teacher extends Member {

    private String initials;

    @Builder(builderMethodName = "teacherBuilder")
    public Teacher(long id, String firstname, String lastname, String email, List<Presentation> presentationsAsExaminator, List<Presentation> presentationsAsExpert, String initials) {
        super(id, firstname, lastname, email);
        this.initials = initials;
    }

}
