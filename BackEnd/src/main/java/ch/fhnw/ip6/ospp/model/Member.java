package ch.fhnw.ip6.ospp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Member {

    @Id
    private long id;
    private String firstname;
    private String lastname;
    private String email;

}
