package ru.tpu.hostel.booking.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record TimeSlotResponseDto(
        UUID id,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
