package ru.tpu.hostel.booking.dto.response;

import ru.tpu.hostel.booking.enums.BookingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingResponseWithUserDto(
        UUID id,
        UUID userId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BookingStatus status,
        String type
) {
}
