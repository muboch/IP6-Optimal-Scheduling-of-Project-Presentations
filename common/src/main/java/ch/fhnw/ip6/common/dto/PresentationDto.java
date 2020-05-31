package ch.fhnw.ip6.common.dto;

import ch.fhnw.ip6.common.dto.marker.P;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PresentationDto implements P {

    private int id;
    private String nr;
    private String name;
    private String schoolclass;
    private String name2;
    private String schoolclass2;
    private String title;
    @JsonIgnore
    private LecturerDto coach;
    @JsonIgnore
    private LecturerDto expert;
    private String coachInitials;
    private String expertInitials;
    private String type;

    @Override
    public String toString() {
        return String.format("P[id=%03d,nr=%s].E%s.C%s", id, nr, expert.toString(), coach.toString());
    }
}
