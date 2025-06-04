CREATE INDEX idx_bookings_user_status_start ON booking.bookings ("user", status, start_time);

CREATE INDEX idx_bookings_status_start ON booking.bookings (status, start_time);

CREATE UNIQUE INDEX idx_bookings_user_timeslot_status
    ON booking.bookings (time_slot_id, "user")
    WHERE status = 'BOOKED';

DROP TRIGGER IF EXISTS trg_prevent_duplicate_bookings ON "booking"."bookings";

DROP FUNCTION IF EXISTS prevent_duplicate_bookings();