package ch.fhnw.ip6.ospp.model;


import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class Teacher extends User {

    private String initials;

    private String firstname;

    private String lastname;

    @Builder(builderMethodName = "teacherBuilder")
    public Teacher(long id, String firstname, String lastname, String email, List<Presentation> presentationsAsExaminator, List<Presentation> presentationsAsExpert, String initials) {
        super(id, email);
        this.firstname = firstname;
        this.lastname = lastname;
        this.initials = initials;
    }

}
