package ch.fhnw.ip6.ospp.vo;

import ch.fhnw.ip6.common.dto.StatusEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlanningVO {

    private Long id;

    private String nr;

    private String name;

    private StatusEnum status;

    private String created;

}
