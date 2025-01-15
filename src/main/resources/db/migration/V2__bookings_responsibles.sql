create table "booking"."responsibles"
(
    id     uuid         not null unique primary key,
    "type"   varchar(255) not null,
    "date" date         not null,
    "user" uuid,
    constraint fk_responsible_on_user foreign key ("user") references "user"."users" (id) on delete set null,
    constraint uq_responsible_type_and_date unique ("type", "date")
)