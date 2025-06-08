package ru.tpu.hostel.booking.config.amqp;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "queueing.schedules-service.timeslot")
public record RabbitScheduleServiceTimeslotQueueingProperties(

        @NotEmpty
        String exchangeName,

        @NotEmpty
        String queueName,

        @NotEmpty
        String routingKey

) {
}
