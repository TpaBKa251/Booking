package ru.tpu.hostel.booking.dto.response;

import java.util.UUID;

public record TimeSlotResponseDto(
        UUID id,
        String time
) {
}
