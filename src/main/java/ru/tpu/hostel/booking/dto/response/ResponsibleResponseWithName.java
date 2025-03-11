package ru.tpu.hostel.booking.dto.response;

public record ResponsibleResponseWithName(
        String firstName,
        String lastName,
        String middleName
) {
}
