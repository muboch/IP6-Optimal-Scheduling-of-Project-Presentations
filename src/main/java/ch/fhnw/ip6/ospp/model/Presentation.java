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
@ToString(exclude = {"student","examinator", "expert"})
public class Presentation {

    @Id
    private long id;

    @ManyToOne
    private Room room;

    private String title;

    @OneToOne
    private Student student;

    @ManyToOne
    private Teacher examinator;

    @ManyToOne
    private Teacher expert;

    @ManyToOne
    private Timeslot timeslot;

}
