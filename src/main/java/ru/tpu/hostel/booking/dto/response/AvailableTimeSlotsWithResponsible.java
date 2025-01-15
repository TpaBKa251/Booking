package ru.tpu.hostel.booking.dto.response;

import java.util.List;
import java.util.UUID;

public record AvailableTimeSlotsWithResponsible(
        UUID responsibleId,
        List<TimeSlotResponseDto> timeSlots
) {
}
