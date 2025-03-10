package ru.tpu.hostel.booking.dto.response;

import java.util.UUID;

public record UserShortResponse(
        UUID id,
        String firstName,
        String lastName,
        String middleName
) {
}
