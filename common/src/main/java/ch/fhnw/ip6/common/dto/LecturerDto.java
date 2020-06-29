package ch.fhnw.ip6.common.dto;

import ch.fhnw.ip6.common.dto.marker.L;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LecturerDto implements L {

    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private String initials;

    public String getName() {
        return lastname + " " + firstname;
    }

    @Override
    public String toString() {
        return String.format("L[id=%03d,ini=%s]", id, initials);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LecturerDto that = (LecturerDto) o;
        return id == that.id &&
                Objects.equals(firstname, that.firstname) &&
                Objects.equals(lastname, that.lastname) &&
                Objects.equals(email, that.email) &&
                Objects.equals(initials, that.initials);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstname, lastname, email, initials);
    }
}
