package ch.fhnw.ip6.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class Timeslot {

     private int id;
     private String date;
     private int block;
     private int priority;

     @Override
     public String toString() {
          return String.format("T[id=%03d,dt='%s']", id, date);
     }
}
