

create table if not exists presentation
(
    id                bigint       not null,
    field             int          null,
    nr                varchar(255) null,
    title             varchar(255) null,
    coach_id          bigint       null,
    expert_id         bigint       null,
    first_student_id  bigint       null,
    room_id           bigint       null,
    second_student_id bigint       null,
    timeslot_id       bigint       null,
    primary key (id),
    constraint FK1hspb7ggnlmvtt1vykil4v4o0
        foreign key (first_student_id) references student (id),
    constraint FK6oxrl5x717m44lt522somxejp
        foreign key (second_student_id) references student (id),
    constraint FK83r663655puusox0bukc144lm
        foreign key (expert_id) references teacher (id),
    constraint FK9xhjwj4h31blp528xyuwuvk8o
        foreign key (coach_id) references teacher (id),
    constraint FKkj91cw2b5o62p24pwtvruvg7m
        foreign key (room_id) references room (id),
    constraint FKko5mujlnitknj7bpyihtxdxtr
        foreign key (timeslot_id) references timeslot (id)
);