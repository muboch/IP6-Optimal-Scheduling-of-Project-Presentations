create table if not exists student
(
    id           bigint       not null,
    email        varchar(255) null,
    firstname    varchar(255) null,
    lastname     varchar(255) null,
    discipline   int          null,
    school_class varchar(255) null,
    primary key (id)
);