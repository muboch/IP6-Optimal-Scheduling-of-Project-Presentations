package ch.fhnw.ip6.ospp.vo;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LecturerVO {

    private Long id;

    private String initials;

    private String firstname;

    private String lastname;

    private List<Long> offtimes;


}
