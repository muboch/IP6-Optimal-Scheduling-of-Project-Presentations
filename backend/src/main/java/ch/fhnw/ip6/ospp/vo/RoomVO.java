package ch.fhnw.ip6.ospp.vo;

import ch.fhnw.ip6.ospp.model.Type;

import java.util.List;


public interface RoomVO {

    long getId();

    String getNr();

    String getPlace();

    Type getType();

    boolean isReserve();

    List<PresentationVO> getPresentations();

}
