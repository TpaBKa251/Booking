package ru.tpu.hostel.booking.dto.response;

import java.time.LocalDateTime;

public record BookingShortResponse(
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
