package ch.fhnw.ip6.ospp.vo;

import java.time.LocalDateTime;
import java.util.List;


public interface TimeslotVO {

    long getId();

    LocalDateTime getBegin();

    LocalDateTime getEnd();

    List<PresentationVO> getPresentations();

}
