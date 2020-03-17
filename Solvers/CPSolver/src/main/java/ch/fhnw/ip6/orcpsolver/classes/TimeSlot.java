package ch.fhnw.ip6.orcpsolver.classes;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class TimeSlot
{
    public TimeSlot(int id, String datum, int block) {
        Id = id;
        Datum = datum;
        Block = block;
    }

    public int Id;
    public String Datum;
    public int Block;
}
