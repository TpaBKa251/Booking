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
    CONSTRAINT fk_booking_on_timeslot FOREIGN KEY (time_slot_id) REFERENCES "booking"."time_slots" (id) ON DELETE CASCADE,
    CONSTRAINT chk_booking_start_end CHECK (start_time < end_time),
    CONSTRAINT chk_booking_status CHECK (status IN ('NOT_BOOKED', 'BOOKED', 'IN_PROGRESS', 'CANCELLED', 'COMPLETED')),
    CONSTRAINT chk_booking_type CHECK (type IN ('HALL', 'INTERNET', 'GYM'))
);

CREATE TABLE "booking"."responsibles"
(
    id     UUID         NOT NULL UNIQUE PRIMARY KEY,
    "type" VARCHAR(255) NOT NULL,
    "date" DATE         NOT NULL,
    "user" UUID,
    CONSTRAINT uq_responsible_type_and_date UNIQUE ("type", "date")
);

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
    CONSTRAINT chk_bookings_start_end CHECK (start_time < end_time),
    CONSTRAINT chk_bookings_status CHECK (status IN ('NOT_BOOKED', 'BOOKED', 'IN_PROGRESS', 'CANCELLED', 'COMPLETED')),
    CONSTRAINT chk_bookings_type CHECK (type IN ('HALL', 'INTERNET', 'GYM'))
);

CREATE OR REPLACE FUNCTION prevent_duplicate_bookings()
    RETURNS TRIGGER AS
$$
BEGIN
    IF EXISTS (SELECT 1
               FROM "booking"."bookings"
               WHERE "user" = NEW."user"
                 AND time_slot_id = NEW.time_slot_id
                 AND status = 'BOOKED') THEN
        RAISE EXCEPTION 'Пользователь уже забронировал этот слот';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_prevent_duplicate_bookings
    BEFORE INSERT
    ON "booking"."bookings"
    FOR EACH ROW
EXECUTE FUNCTION prevent_duplicate_bookings();

CREATE OR REPLACE FUNCTION prevent_duplicate_booking()
    RETURNS TRIGGER AS
$$
BEGIN
    IF EXISTS (SELECT 1
               FROM "booking"."booking"
               WHERE "user" = NEW."user"
                 AND time_slot_id = NEW.time_slot_id
                 AND status = 'BOOKED') THEN
        RAISE EXCEPTION 'Пользователь уже забронировал этот слот';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_prevent_duplicate_booking
    BEFORE INSERT
    ON "booking"."booking"
    FOR EACH ROW
EXECUTE FUNCTION prevent_duplicate_booking();