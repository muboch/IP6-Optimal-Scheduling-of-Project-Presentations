create table if not exists room
(
    id          bigint       not null,
    room_number varchar(255) null,
    room_type   varchar(255) null,
    primary key (id)
);