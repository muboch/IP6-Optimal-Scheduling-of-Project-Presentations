package ch.fhnw.ip6.ospp.model;


import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class Lecturer extends User {

    private String initials;


    private int externalId;

    private String firstname;

    private String lastname;

    @OneToMany
    private List<Timeslot> locktimes;

    @Builder(builderMethodName = "lecturerBuilder")
    public Lecturer(long id, String firstname, String lastname, String email, List<Presentation> presentationsAsExaminator, List<Presentation> presentationsAsExpert, String initials, int externalId) {
        super(id, email);
        this.firstname = firstname;
        this.externalId = externalId;
        this.lastname = lastname;
        this.initials = initials;
    }

}
