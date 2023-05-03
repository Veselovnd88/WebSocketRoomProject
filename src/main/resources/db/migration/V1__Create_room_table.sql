CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TABLE room
(
    id         UUID                         DEFAULT gen_random_uuid(),
    name       varchar(100) UNIQUE NOT NULL,
    is_private boolean             NOT NULL DEFAULT FALSE,
    owner_name varchar(255)        NOT NULL,
    room_token varchar(255) UNIQUE,
    source_url varchar,
    CONSTRAINT rooms_pk PRIMARY KEY (id)
);

INSERT INTO public.room (name, is_private, owner_name, room_token, source_url)
VALUES ('Самая первая комната', false, 'user1', null, 'https://youtu.be/JV2JcHYMnKk'),
       ('Комната 2', false, 'user1', null, 'https://youtu.be/IGKgBZUkfwk'),
       ('Приватная комната', true, 'vasya', 'token', 'https://youtu.be/G0fBKHXNx-Y'),
       ('Приватная 2', true, 'user1', 'token1', 'https://youtu.be/MomSITt84wY');