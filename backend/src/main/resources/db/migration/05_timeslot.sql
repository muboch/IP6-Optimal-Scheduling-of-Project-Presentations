

create table if not exists timeslot
(
    id    bigint      not null,
    start datetime(6) null,
    primary key (id)
);