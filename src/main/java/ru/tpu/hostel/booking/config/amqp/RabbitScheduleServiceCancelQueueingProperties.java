package ru.tpu.hostel.booking.config.amqp;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import ru.tpu.hostel.booking.external.amqp.MessageType;
import ru.tpu.hostel.booking.external.amqp.schedule.ScheduleMessageType;

@Validated
@ConfigurationProperties(prefix = "queueing.schedules-service.cancel")
public record RabbitScheduleServiceCancelQueueingProperties(

        @NotEmpty
        String exchangeName,

        @NotEmpty
        String routingKey

) implements QueueingProperties {

    @Override
    public boolean isApplicable(MessageType type) {
        return type == ScheduleMessageType.CANCEL;
    }
}
