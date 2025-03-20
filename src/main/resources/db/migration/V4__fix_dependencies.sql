ALTER TABLE "booking"."booking"
    DROP CONSTRAINT fk_booking_on_user,
    ADD CONSTRAINT chk_booking_start_end CHECK (start_time < end_time),
    ADD CONSTRAINT chk_booking_status CHECK (status IN
                                             ('NOT_BOOKED', 'BOOKED', 'IN_PROGRESS', 'CANCELLED', 'COMPLETED')),
    ADD CONSTRAINT chk_booking_type CHECK (type IN ('HALL', 'INTERNET', 'GYM'));

ALTER TABLE "booking"."responsibles"
    DROP CONSTRAINT fk_responsible_on_user;

ALTER TABLE "booking"."bookings"
    DROP CONSTRAINT fk_bookings_on_user,
    DROP CONSTRAINT fk_bookings_on_timeslot,
    ADD CONSTRAINT chk_bookings_start_end CHECK (start_time < end_time),
    ADD CONSTRAINT chk_bookings_status CHECK (status IN
                                              ('NOT_BOOKED', 'BOOKED', 'IN_PROGRESS', 'CANCELLED', 'COMPLETED')),
    ADD CONSTRAINT chk_bookings_type CHECK (type IN ('HALL', 'INTERNET', 'GYM'));

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
    ON "booking"."bookings"
    FOR EACH ROW
EXECUTE FUNCTION prevent_duplicate_booking();

