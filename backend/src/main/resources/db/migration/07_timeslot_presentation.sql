
create table if not exists timeslot_presentations
(
    timeslot_id      bigint not null,
    presentations_id bigint not null,
    constraint UK_8pjbm9agj0xs6k7rtkjywgw46
        unique (presentations_id),
    constraint FK3ead2yb3cg165j25jd3vn533h
        foreign key (presentations_id) references presentation (id),
    constraint FKrmdqauuuk0ik0vawjb3wt852q
        foreign key (timeslot_id) references timeslot (id)
);

