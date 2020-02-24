package ch.fhnw.ip6.osfpp.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.sql.Clob;

@Entity
@Data
public class Presentation {

    @Id
    private long id;

    @ManyToOne
    private Room room;
    //private Timeslot timeslot;
    private String title;
    private Clob text;

    @OneToOne
    private Student student;

    @ManyToOne
    private Teacher examinator;
    @ManyToOne
    private Teacher expert;

}
