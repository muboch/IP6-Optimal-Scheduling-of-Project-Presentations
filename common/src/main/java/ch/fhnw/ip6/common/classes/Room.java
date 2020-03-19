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

       private int id;
       private String name;
       private String place;
       private String type;
       private Boolean reserve;

}
