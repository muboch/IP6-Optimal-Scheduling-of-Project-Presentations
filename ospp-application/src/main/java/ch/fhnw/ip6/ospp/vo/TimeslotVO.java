package ch.fhnw.ip6.ospp.vo;

import java.util.List;


public interface TimeslotVO {

    int getId();

    String getDate();

    int getBlock();

    List<PresentationVO> getPresentations();

}
