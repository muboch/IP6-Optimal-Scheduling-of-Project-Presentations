package ch.fhnw.ip6.common.classes;

import lombok.*;

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
