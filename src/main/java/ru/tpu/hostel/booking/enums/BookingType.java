package ru.tpu.hostel.booking.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Deprecated()
@RequiredArgsConstructor
@Getter
public enum BookingType {
    HALL("Зал"),
    INTERNET("Интернет"),
    GYM("Тренажерный зал"),
    KITCHEN("Кухня");

    private final String bookingTypeName;
}
