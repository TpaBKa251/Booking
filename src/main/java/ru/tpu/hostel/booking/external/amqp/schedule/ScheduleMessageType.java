package ru.tpu.hostel.booking.external.amqp.schedule;

import ru.tpu.hostel.booking.external.amqp.MessageType;

public enum ScheduleMessageType implements MessageType {
    BOOK,
    CANCEL
}
