package ru.tpu.hostel.booking.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Статусы брони
 */
@RequiredArgsConstructor
@Getter
public enum BookingStatus {

    NOT_BOOKED("Не забронировано"),
    BOOKED("Предстоящее"),
    IN_PROGRESS("В процессе"),
    CANCELLED("Отменено"),
    COMPLETED("Завершено");

    private final String bookingStatusName;

}
