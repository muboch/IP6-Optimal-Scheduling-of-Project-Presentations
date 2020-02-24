package ch.fhnw.ip6.osfpp.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
@Data
public class Student extends Member{

    @OneToOne
    private Presentation presentation;

}
