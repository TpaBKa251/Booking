package ru.tpu.hostel.booking.dto.response;

import ru.tpu.hostel.booking.enums.BookingType;

import java.time.LocalDate;
import java.util.UUID;

public record ResponsibleResponseDto(
        LocalDate date,
        BookingType type,
        UUID user
) {
}