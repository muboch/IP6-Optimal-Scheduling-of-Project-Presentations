package ch.fhnw.ip6.ospp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Version;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"firstStudent","secondStudent","coach", "expert"})
public class Presentation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @CreationTimestamp
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime updated;

    @Version
    private int version;

    private String nr;

    @ManyToOne
    private Room room;

    private String title;

    @OneToOne(cascade = CascadeType.ALL)
    private Student firstStudent;

    @OneToOne(cascade = CascadeType.ALL)
    private Student secondStudent;

    @ManyToOne
    private Lecturer coach;

    @ManyToOne
    private Lecturer expert;

    @ManyToOne
    private Timeslot timeslot;

    private Type type;

}