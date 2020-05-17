package ch.fhnw.ip6.ospp.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoomVO {

    private Long id;

    private String name;

    private String place;

    private String type;

    private boolean reserve;

    private List<Long> presentations;

}
