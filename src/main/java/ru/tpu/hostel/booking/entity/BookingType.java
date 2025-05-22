package ru.tpu.hostel.booking.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Типы брони (KITCHEN удалится, после полного переноса слотов и ответственных в Вахту - сервис расписаний)
 */
@RequiredArgsConstructor
@Getter
public enum BookingType {

    HALL("Зал"),
    INTERNET("Интернет"),
    GYM("Тренажерный зал");

    private final String bookingTypeName;

}
