package ru.tpu.hostel.booking.dto.response;

import java.time.LocalDateTime;

public record AvailableTimeBookingDto(
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
