package ru.tpu.hostel.booking.external.amqp.schedule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import ru.tpu.hostel.booking.common.exception.ServiceException;
import ru.tpu.hostel.booking.common.utils.TimeUtil;
import ru.tpu.hostel.booking.config.amqp.QueueingProperties;
import ru.tpu.hostel.booking.external.amqp.MessageSender;
import ru.tpu.hostel.booking.external.amqp.MessageType;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class AbstractAmqpMessageSender<T extends MessageType, R> implements MessageSender<T, R> {

    protected static final int PRIORITY = 10;

    protected final RabbitTemplate rabbitTemplate;

    protected final Set<QueueingProperties> queueingProperties;

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .setTimeZone(TimeUtil.getTimeZone())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @Override
    public void send(T messageType, String messageId, Object messagePayload) throws IOException {
        MessageProperties messageProperties = getMessageProperties(messageId);
        Message message = new Message(MAPPER.writeValueAsBytes(messagePayload), messageProperties);
        configureRabbitTemplate(messageType);
        rabbitTemplate.send(message);
    }

    @Override
    public R sendAndReceive(T messageType, String messageId, Object messagePayload) throws IOException {
        MessageProperties messageProperties = getMessageProperties(messageId);
        Message message = new Message(MAPPER.writeValueAsBytes(messagePayload), messageProperties);
        configureRabbitTemplate(messageType);
        Message response = rabbitTemplate.sendAndReceive(message);

        if (response != null) {
            return MAPPER.readValue(response.getBody(), new TypeReference<>() {});
        }
        throw new IOException("Ответ от пустой");
    }

    protected MessageProperties getMessageProperties(String messageId) {
        ZonedDateTime now = TimeUtil.getZonedDateTime();
        long nowMillis = now.toInstant().toEpochMilli();

        return MessagePropertiesBuilder.newInstance()
                .setMessageId(messageId)
                .setCorrelationId(UUID.randomUUID().toString())
                .setPriority(PRIORITY)
                .setTimestamp(new Date(nowMillis))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .build();
    }

    protected void configureRabbitTemplate(MessageType messageType) {
        QueueingProperties properties = queueingProperties.stream()
                .filter(queueingProperties1 -> queueingProperties1.isApplicable(messageType))
                .findFirst()
                .orElseThrow(
                        () -> new ServiceException(
                                "Конфигурация очереди для " + messageType + " не найдена",
                                HttpStatus.NOT_IMPLEMENTED
                        )
                );
        rabbitTemplate.setExchange(properties.exchangeName());
        rabbitTemplate.setRoutingKey(properties.routingKey());
    }


}
