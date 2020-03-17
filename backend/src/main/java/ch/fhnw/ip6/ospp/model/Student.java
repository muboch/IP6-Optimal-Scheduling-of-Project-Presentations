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
public class Student extends Member {

    private String schoolClass;

    private Discipline discipline;

    @Builder(builderMethodName = "studentBuilder")
    public Student(long id, String firstname, String lastname, String email, String schoolClass, Discipline discipline) {
        super(id, firstname, lastname, email);
        this.schoolClass = schoolClass;
        this.discipline = discipline;
    }

}
