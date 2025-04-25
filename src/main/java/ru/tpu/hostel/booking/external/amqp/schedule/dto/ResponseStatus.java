package ru.tpu.hostel.booking.external.amqp.schedule.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ResponseStatus {

    SUCCESS("Успех"),
    FAILURE("Ошибка");

    private final String status;
}
