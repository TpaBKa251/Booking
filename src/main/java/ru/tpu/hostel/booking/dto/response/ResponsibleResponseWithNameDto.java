package ru.tpu.hostel.booking.dto.response;

public record ResponsibleResponseWithNameDto(
        String firstName,
        String lastName,
        String middleName
) {
}
