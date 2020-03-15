package ch.fhnw.ip6.ospp.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;


@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Room {

    @Id
    private long id;

    private String roomNumber;

    private String roomType;

    @OneToMany(mappedBy = "room")
    private List<Presentation> presentations;

}
