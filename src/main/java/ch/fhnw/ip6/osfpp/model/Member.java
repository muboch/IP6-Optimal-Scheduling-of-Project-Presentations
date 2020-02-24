package ch.fhnw.ip6.osfpp.model;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
public abstract class Member {

    @Id
    private long id;
    private String firstname;
    private String lastname;
    private String email;

}
