package ru.tpu.hostel.booking.common.utils;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

/**
 * Утилита для работы с временем
 */
@UtilityClass
public final class TimeUtil {

    private static final ZoneId UTC7_ZONE = ZoneId.of("UTC+7");

    public static LocalDateTime now() {
        return ZonedDateTime.now(UTC7_ZONE).toLocalDateTime();
    }

    public static TimeZone getTimeZone() {
        return TimeZone.getTimeZone(UTC7_ZONE);
    }

    public static ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.now(UTC7_ZONE);
    }

    public static String getLocalDateTimeStingFromMillis(long millis) {
        return LocalDateTime
                .ofInstant(Instant.ofEpochMilli(millis), UTC7_ZONE)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

}
