create table if not exists plan
(
    id      bigint       not null,
    created datetime(6)  null,
    plan_nr varchar(255) null,
    primary key (id)
);
