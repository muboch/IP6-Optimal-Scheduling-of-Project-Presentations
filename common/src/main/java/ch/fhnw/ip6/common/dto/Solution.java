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

    private Room room;
    private Timeslot timeSlot;
    private Presentation presentation;
    private Lecturer expert;
    private Lecturer coach;
}
