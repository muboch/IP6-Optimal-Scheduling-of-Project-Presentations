package ch.fhnw.ip6.common.dto;

import ch.fhnw.ip6.common.dto.marker.L;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LecturerDto implements L {

    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private String initials;

    public String getName(){
        return lastname + " " + firstname;
    }

    @Override
    public String toString() {
        return String.format("L[id=%03d,ini=%s]",id, initials);
    }
}
