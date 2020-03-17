package ch.fhnw.ip6.ospp.vo;

import ch.fhnw.ip6.ospp.model.RoomType;

import java.util.List;


public interface RoomVO {

    long getId();

    String getRoomNumber();

    RoomType getRoomType();

    List<PresentationVO> getPresentations();

}
