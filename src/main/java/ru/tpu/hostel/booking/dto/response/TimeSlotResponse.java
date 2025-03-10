package ru.tpu.hostel.booking.dto.response;

import java.util.UUID;

public record TimeSlotResponse(
        UUID id,
        String time
) {
}
