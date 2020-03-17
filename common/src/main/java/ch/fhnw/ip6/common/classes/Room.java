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
public class Room {

       private long id;
       private String name;
       private String place;
       private boolean musicroom;
       private boolean danceroom;
       private boolean artroom;
       private boolean reserve;

}
