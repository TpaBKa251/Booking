CREATE SCHEMA IF NOT EXISTS "booking";

CREATE TABLE "booking"."time_slots"
(
    id         UUID                        NOT NULL UNIQUE,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    type       VARCHAR(255)                NOT NULL,
    "limit"    INTEGER                     NOT NULL,
    CONSTRAINT pk_time_slots PRIMARY KEY (id)
);

CREATE TABLE "booking"."booking"
(
    id           UUID                        NOT NULL UNIQUE,
    start_time   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status       VARCHAR(255),
    type         VARCHAR(255),
    "user"       UUID,
    time_slot_id UUID,
    CONSTRAINT pk_booking PRIMARY KEY (id),
    CONSTRAINT fk_booking_on_user FOREIGN KEY ("user") REFERENCES "user"."users" (id) ON DELETE SET NULL,
    CONSTRAINT fk_booking_on_timeslot FOREIGN KEY (time_slot_id) REFERENCES "schedules"."time_slots" (id) ON DELETE CASCADE
);
