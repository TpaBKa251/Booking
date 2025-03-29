package ru.tpu.hostel.booking.dto.response;

import java.util.List;
import java.util.UUID;

@Deprecated(forRemoval = true)
public record AvailableTimeSlotsWithResponsible(
        UUID responsibleId,
        List<TimeSlotResponse> timeSlots
) {
}
