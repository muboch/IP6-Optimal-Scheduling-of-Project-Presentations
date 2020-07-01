package ch.fhnw.ip6.common.dto;

import ch.fhnw.ip6.common.dto.marker.T;
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
public class TimeslotDto implements T {

     private int id;
     private String date;
     private int block;
     private int priority;
     private int sortOrder;


     @Override
     public boolean equals(Object o) {
          if (this == o) return true;
          if (o == null || getClass() != o.getClass()) return false;
          TimeslotDto that = (TimeslotDto) o;
          return id == that.id &&
                  block == that.block &&
                  priority == that.priority &&
                  sortOrder == that.sortOrder &&
                  Objects.equals(date, that.date);
     }

     @Override
     public int hashCode() {
          return Objects.hash(id, date, block, priority, sortOrder);
     }
}
