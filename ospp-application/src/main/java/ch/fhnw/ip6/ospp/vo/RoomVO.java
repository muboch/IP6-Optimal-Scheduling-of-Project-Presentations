package ch.fhnw.ip6.ospp.vo;

import ch.fhnw.ip6.ospp.model.Type;

import java.util.List;


public interface RoomVO {

    int getId();

    String getName();

    String getPlace();
    int getExternalId();
    Type getType();

    boolean isReserve();

    List<PresentationVO> getPresentations();

}
