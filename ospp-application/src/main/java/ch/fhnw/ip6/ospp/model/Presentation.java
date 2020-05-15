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
import javax.validation.constraints.NotNull;
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
    
    private int externalId;

    @CreationTimestamp
    private LocalDateTime created;

    @UpdateTimestamp
    private LocalDateTime updated;

    @Version
    private int version;

    @NotNull(message = "Nr ist zwingend.")
    private String nr;

    @ManyToOne
    private Room room;

    @NotNull(message = "Titel ist zwingend.")
    private String title;

    @NotNull(message = "Schüler 1 ist zwingend.")
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    private Student studentOne;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.DETACH, CascadeType.REFRESH})
    private Student studentTwo;

    @ManyToOne
    private Lecturer coach;

    @ManyToOne
    private Lecturer expert;

    @ManyToOne
    private Timeslot timeslot;

    @NotNull(message = "Typ ist zwingend.")
    private String type;

}
