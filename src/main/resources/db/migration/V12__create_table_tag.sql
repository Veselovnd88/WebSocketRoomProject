create table tag
(
    tag_id bigserial           not null,
    tag    varchar(255) UNIQUE NOT NULL DEFAULT 'Other',
    primary key (tag_id),
    created_at  timestamp with time zone
);

create table room_tag
(
    room_id UUID      NOT NULL,
    tag_id  bigserial NOT NULL,
    primary key (room_id, tag_id),
    constraint room_tag_fk1
        foreign key (room_id) references public.room (id),
    constraint room_tag_fk2
        foreign key (tag_id) references public.tag (tag_id)
)