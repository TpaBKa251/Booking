package ru.tpu.hostel.booking.external.amqp;

import java.io.IOException;

public interface MessageSender<T extends MessageType, R> {

    void send(T messageType, String messageId, Object messagePayload) throws IOException;

    R sendAndReceive(T messageType, String messageId, Object messagePayload) throws IOException;

}
