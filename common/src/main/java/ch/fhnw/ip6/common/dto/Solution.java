package ch.fhnw.ip6.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Solution {

    private RoomDto room;
    private TimeslotDto timeSlot;
    private PresentationDto presentation;
    private LecturerDto expert;
    private LecturerDto coach;
}
