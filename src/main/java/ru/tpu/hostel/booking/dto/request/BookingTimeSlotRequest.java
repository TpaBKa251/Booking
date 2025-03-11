package ru.tpu.hostel.booking.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record BookingTimeSlotRequest(
        @NotNull(message = "Номер слота не может быть пустым")
        UUID slotId
) {
}
