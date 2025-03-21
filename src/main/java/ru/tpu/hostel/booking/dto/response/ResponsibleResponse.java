package ru.tpu.hostel.booking.dto.response;

import ru.tpu.hostel.booking.entity.BookingType;

import java.time.LocalDate;
import java.util.UUID;

public record ResponsibleResponse(
        LocalDate date,
        BookingType type,
        UUID user
) {
}
