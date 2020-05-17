package ch.fhnw.ip6.common.dto;

import ch.fhnw.ip6.common.dto.marker.R;
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
public class RoomDto implements R {

    private int id;
    private String name;
    private String place;
    private String type;
    private Boolean reserve;

    @Override
    public String toString() {
        return String.format("R[id=%02d,name=%s]", id, name);
    }
}
