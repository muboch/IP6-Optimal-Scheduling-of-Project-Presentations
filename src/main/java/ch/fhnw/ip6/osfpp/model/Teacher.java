package ch.fhnw.ip6.osfpp.model;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Data
public class Teacher extends Member {

    @OneToMany(mappedBy = "examinator")
    private List<Presentation> presentationsAsExaminator;


    @OneToMany(mappedBy = "expert")
    private List<Presentation> presentationsAsExpert;

}
