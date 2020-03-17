package ch.fhnw.ip6.orcpsolver.classes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor

public class Presentation {
    public int Id;

    @JsonProperty("Nr.")
    public String Nr;

    public String Name;

    @JsonProperty("Klasse")
    public String SchoolClass;

    @JsonProperty("Name 2")
    public String Name2;

    @JsonProperty("Klasse2")
    public String SchoolClass2;

    public String Type;

    @JsonProperty("Titel")
    public String Title;

    @JsonProperty("Exam")
    public String SupervisorShort;

    @JsonProperty("Exp")
    public String ExpertShort;

    @JsonIgnore
    public Coach Supervisor;
    @JsonIgnore
    public Coach Expert;

}
