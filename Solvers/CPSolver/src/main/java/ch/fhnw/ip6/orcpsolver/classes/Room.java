package ch.fhnw.ip6.orcpsolver.classes;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class Room {
    public Room(int id, String name, String ort, String type, Boolean reserve) {
        Id = id;
        this.name = name;
        this.ort = ort;
        Type = type;
        this.reserve = reserve;
    }

    public int Id;
    public String name;
    public String ort;
    public String Type;
    public Boolean reserve;

}
