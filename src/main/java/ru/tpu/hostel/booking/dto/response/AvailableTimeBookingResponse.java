package ru.tpu.hostel.booking.dto.response;

import java.time.LocalDateTime;

public record AvailableTimeBookingResponse(
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
