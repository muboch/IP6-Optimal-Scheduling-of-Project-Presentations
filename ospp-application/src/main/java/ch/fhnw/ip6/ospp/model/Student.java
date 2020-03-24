package ch.fhnw.ip6.ospp.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class Student extends User {

    private String schoolclass;
    private String name;

    @Builder(builderMethodName = "studentBuilder")
    public Student(long id, String name, String email, String schoolclass) {
        super(id, email);
        this.name = name;
        this.schoolclass = schoolclass;
    }

}
