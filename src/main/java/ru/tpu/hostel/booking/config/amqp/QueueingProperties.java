package ru.tpu.hostel.booking.config.amqp;

import ru.tpu.hostel.booking.external.amqp.MessageType;

public interface QueueingProperties {

    String exchangeName();

    String routingKey();

    boolean isApplicable(MessageType type);

}
