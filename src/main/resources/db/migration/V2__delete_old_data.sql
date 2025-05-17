DROP TRIGGER IF EXISTS trg_prevent_duplicate_booking ON "booking"."booking";

DROP FUNCTION IF EXISTS prevent_duplicate_booking();

DROP TABLE IF EXISTS "booking"."booking";

DROP TABLE IF EXISTS "booking"."time_slots";

DROP TABLE IF EXISTS "booking"."responsibles";