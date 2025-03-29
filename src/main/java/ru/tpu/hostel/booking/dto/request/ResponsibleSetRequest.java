package ru.tpu.hostel.booking.dto.request;

import jakarta.validation.constraints.NotNull;
import ru.tpu.hostel.booking.entity.BookingType;

import java.time.LocalDate;
import java.util.UUID;

@Deprecated(forRemoval = true)
public record ResponsibleSetRequest(
        @NotNull(message = "Дата назначения ответственного не может быть пустой")
        LocalDate date,

        @NotNull(message = "Тип ответственного не может быть пустым")
        BookingType type,

        @NotNull(message = "ID ответственного не может быть пустым")
        UUID user
) {
}
