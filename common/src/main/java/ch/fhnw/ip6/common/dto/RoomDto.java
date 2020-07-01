package ch.fhnw.ip6.common.dto;

import ch.fhnw.ip6.common.dto.marker.R;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto implements R {

    private int id;
    private String name;
    private String place;
    private String type;
    private Boolean reserve;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomDto roomDto = (RoomDto) o;
        return id == roomDto.id &&
                Objects.equals(name, roomDto.name) &&
                Objects.equals(place, roomDto.place) &&
                Objects.equals(type, roomDto.type) &&
                Objects.equals(reserve, roomDto.reserve);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, place, type, reserve);
    }
}
