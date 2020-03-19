package ch.fhnw.ip6.common.classes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Lecturer {

    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private String initials;

}
