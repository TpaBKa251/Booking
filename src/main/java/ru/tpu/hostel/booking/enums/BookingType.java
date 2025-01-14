package ru.tpu.hostel.booking.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BookingType {
    HALL("Зал"),
    INTERNET("Интернет"),
    GYM("Тренажерный зал");

    private final String bookingTypeName;
}
