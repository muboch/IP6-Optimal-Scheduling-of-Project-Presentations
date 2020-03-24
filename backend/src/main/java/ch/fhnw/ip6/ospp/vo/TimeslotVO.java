package ch.fhnw.ip6.ospp.vo;

import java.time.LocalDateTime;
import java.util.List;


public interface TimeslotVO {

    int getId();

    String getStart();

    int getBlock();

    List<PresentationVO> getPresentations();

}
