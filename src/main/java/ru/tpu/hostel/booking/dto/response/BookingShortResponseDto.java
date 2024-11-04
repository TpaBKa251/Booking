package ru.tpu.hostel.booking.dto.response;

import java.time.LocalDateTime;

public record BookingShortResponseDto(
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
