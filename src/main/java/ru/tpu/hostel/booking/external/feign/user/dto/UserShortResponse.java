package ru.tpu.hostel.booking.external.feign.user.dto;

import java.util.UUID;

public record UserShortResponse(
        UUID id,
        String firstName,
        String lastName,
        String middleName
) {
}
