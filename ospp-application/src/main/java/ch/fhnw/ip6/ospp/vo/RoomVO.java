package ch.fhnw.ip6.ospp.vo;

import java.util.List;


public interface RoomVO {

    int getId();

    String getName();

    String getPlace();
    int getExternalId();
    String getType();

    boolean isReserve();

    List<PresentationVO> getPresentations();

}
