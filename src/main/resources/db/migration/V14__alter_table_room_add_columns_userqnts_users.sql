alter table if exists room
    add column max_user_qnt integer default 0;

alter table if exists room
    add column user_qnt integer default 0;



create table room_users
(
    room_id uuid not null,
    users   varchar(255)
);