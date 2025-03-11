CREATE TABLE "booking"."bookings"
(
    id           UUID                        NOT NULL UNIQUE,
    start_time   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status       VARCHAR(255),
    type         VARCHAR(255),
    "user"       UUID,
    time_slot_id UUID,
    CONSTRAINT pk_bookings PRIMARY KEY (id),
    CONSTRAINT fk_bookings_on_user FOREIGN KEY ("user") REFERENCES "user"."users" (id) ON DELETE SET NULL,
    CONSTRAINT fk_bookings_on_timeslot FOREIGN KEY (time_slot_id) REFERENCES "schedules"."time_slots" (id) ON DELETE CASCADE
);