package ch.fhnw.ip6.common.dto;

import ch.fhnw.ip6.common.dto.marker.T;
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
public class TimeslotDto implements T {

     private int id;
     private String date;
     private int block;
     private int priority;
     private int sortOrder;


     @Override
     public String toString() {
          return String.format("T[id=%03d,dt='%s']", id, date.replace(" ", "-"));
     }
}
