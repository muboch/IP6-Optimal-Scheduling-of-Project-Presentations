

create table if not exists teacher
(
    id        bigint       not null,
    email     varchar(255) null,
    firstname varchar(255) null,
    lastname  varchar(255) null,
    initials  varchar(255) null,
    primary key (id)
);