CREATE TABLE "booking"
(
    id           UUID                        NOT NULL UNIQUE,
    start_time   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    status       VARCHAR(255),
    type         VARCHAR(255),
    "user"       UUID,
    time_slot_id UUID,
    CONSTRAINT pk_booking PRIMARY KEY (id)
);

CREATE TABLE "time_slots"
(
    id         UUID                        NOT NULL UNIQUE,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    type       VARCHAR(255)                NOT NULL,
    "limit"    INTEGER                     NOT NULL,
    CONSTRAINT pk_time_slots PRIMARY KEY (id)
);

ALTER TABLE booking
    ADD CONSTRAINT FK_BOOKING_ON_TIMESLOT FOREIGN KEY (time_slot_id) REFERENCES time_slots (id);