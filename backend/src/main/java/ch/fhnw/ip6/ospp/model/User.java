package ch.fhnw.ip6.ospp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class User extends BaseEntity {

    private String email;

    public User(long id, String email) {
        super(id);
        this.email = email;
    }
}
