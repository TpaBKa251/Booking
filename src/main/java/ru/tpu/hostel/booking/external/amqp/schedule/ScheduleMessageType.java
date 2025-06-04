package ru.tpu.hostel.booking.external.amqp.schedule;

public enum ScheduleMessageType {
    BOOK,
    CANCEL,
    CANCEL_WITHOUT_TRANSACTION
}
