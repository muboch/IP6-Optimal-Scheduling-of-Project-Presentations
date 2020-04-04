package ch.fhnw.ip6.ospp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.sql.Clob;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Planning {

    @Id
    private long id;

    private String nr;

    private String status;

    private String name;

    private LocalDateTime created;

    @Lob
    private byte[] planning;


}
