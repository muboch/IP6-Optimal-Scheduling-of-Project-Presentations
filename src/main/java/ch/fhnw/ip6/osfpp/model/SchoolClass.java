package ch.fhnw.ip6.osfpp.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class SchoolClass {

    @Id
    private long id;
    private String className;

}
