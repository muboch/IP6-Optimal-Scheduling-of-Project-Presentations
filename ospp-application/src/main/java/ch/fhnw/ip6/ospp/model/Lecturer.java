package ch.fhnw.ip6.ospp.model;


import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class Lecturer extends User {

    @NotNull(message = "Initialen ist zwingend.")
    private String initials;

    private int externalId;

    @NotNull(message = "Vorname ist zwingend.")
    private String firstname;

    @NotNull(message = "Nachname ist zwingend.")
    private String lastname;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<Timeslot> offtimes;

    @Builder(builderMethodName = "lecturerBuilder")
    public Lecturer(long id, String firstname, String lastname, String email, List<Presentation> presentationsAsExaminator, List<Presentation> presentationsAsExpert, List<Timeslot> offtimes, String initials, int externalId) {
        super(id, email);
        this.firstname = firstname;
        this.externalId = externalId;
        this.lastname = lastname;
        this.initials = initials;
        this.offtimes = offtimes;
    }

}
