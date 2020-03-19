package ch.fhnw.ip6.common.classes;


public class Solution {
            public Room room;
            public Timeslot timeSlot;
            public Presentation presentation;
            public Lecturer expert;
            public Lecturer coach;

            public Solution(Room room, Timeslot timeSlot, Presentation presentation, Lecturer expert, Lecturer coach)
            {
                this.room = room;
                this.timeSlot = timeSlot;
                this.presentation = presentation;
                this.expert = expert;
                this.coach = coach;
            }



}
