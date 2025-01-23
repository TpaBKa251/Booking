package ru.tpu.hostel.booking.dto.response;

import java.util.UUID;

public record UserShortResponseDto2(
        UUID id,
        String firstName,
        String lastName,
        String middleName
) {
}
