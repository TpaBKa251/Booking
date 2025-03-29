package ru.tpu.hostel.booking.dto.response;

@Deprecated(forRemoval = true)
public record ResponsibleResponseWithName(
        String firstName,
        String lastName,
        String middleName
) {
}
