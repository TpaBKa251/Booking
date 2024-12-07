package ru.tpu.hostel.booking.dto.response;

import ru.tpu.hostel.booking.enums.BookingStatus;
import ru.tpu.hostel.booking.enums.BookingType;

import java.time.LocalDateTime;
import java.util.UUID;

public record BookingResponseDto(
        UUID id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BookingStatus status,
        String type
) {
}
