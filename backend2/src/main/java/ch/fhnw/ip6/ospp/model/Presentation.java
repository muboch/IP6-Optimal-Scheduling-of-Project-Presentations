package ch.fhnw.ip6.ospp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"firstStudent","secondStudent","coach", "expert"})
public class Presentation {

    @Id
    private long id;

    private String nr;

    @ManyToOne
    private Room room;

    private String title;

    @OneToOne
    private Student firstStudent;

    @OneToOne
    private Student secondStudent;

    @ManyToOne
    private Teacher coach;

    @ManyToOne
    private Teacher expert;

    @ManyToOne
    private Timeslot timeslot;

    private Field field;

}
