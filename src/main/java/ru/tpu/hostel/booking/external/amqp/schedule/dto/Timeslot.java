package ru.tpu.hostel.booking.external.amqp.schedule.dto;

import ru.tpu.hostel.booking.entity.BookingType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Модель, которую получаем от Вахты (сервис расписаний)
 *
 * @param id        ID слота
 * @param startTime стартовое время слота
 * @param endTime   конечное время слота
 * @param type      тип брони слота
 * @param limit     лимит броней для слота
 */
public record Timeslot(
        UUID id,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BookingType type,
        Integer limit
) {
}
