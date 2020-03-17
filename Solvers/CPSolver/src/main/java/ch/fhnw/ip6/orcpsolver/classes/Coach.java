package ch.fhnw.ip6.orcpsolver.classes;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Entity;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class Coach {
    public Coach(String vorname, String name, String shortName, String email, int id) {
        vorname = vorname;
        name = name;
        shortname = shortName;
        email = email;
        this.id = id;
    }


    @JsonProperty("Vorname")
    public String vorname;

    @JsonProperty("Name")
    public String name;

    @JsonProperty("ShortName")
    public String shortname;

    @JsonProperty("Email")
    public String email;

    @JsonProperty("Id")
    public int id;
}




