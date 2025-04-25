package ru.tpu.hostel.booking.config.amqp;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tpu.hostel.booking.external.amqp.AmqpMessageSender;
import ru.tpu.hostel.booking.external.amqp.MessageSender;
import ru.tpu.hostel.booking.external.amqp.schedule.ScheduleMessageType;
import ru.tpu.hostel.booking.external.amqp.schedule.RabbitScheduleServiceBookMessageSender;
import ru.tpu.hostel.booking.external.amqp.schedule.RabbitScheduleServiceMessageSender;
import ru.tpu.hostel.booking.external.amqp.schedule.dto.ScheduleResponse;

import java.util.Set;

/**
 * Конфигурация брокера сообщений RabbitMQ для общения с микросервисом расписаний
 */
@Configuration
@Slf4j
@EnableConfigurationProperties({
        RabbitSchedulesServiceProperties.class,
        RabbitScheduleServiceBookQueueingProperties.class,
        RabbitScheduleServiceCancelQueueingProperties.class,
})
public class RabbitScheduleServiceConfiguration {

    public static final String SCHEDULES_SERVICE_LISTENER = "schedulesServiceListener";

    private static final String SCHEDULES_SERVICE_CONNECTION_FACTORY = "schedulesServiceConnectionFactory";

    private static final String SCHEDULES_SERVICE_AMQP_ADMIN = "schedulesServiceAmqpAdmin";

    private static final String SCHEDULES_SERVICE_RABBIT_TEMPLATE = "schedulesServiceRabbitTemplate";

    private static final String SCHEDULES_SERVICE_MESSAGE_CONVERTER = "schedulesServiceMessageConverter";

    private static final String SCHEDULES_SERVICE_BOOK_AMQP_MESSAGE_SENDER = "schedulesServiceBookAmqpMessageSender";

    @Bean(SCHEDULES_SERVICE_MESSAGE_CONVERTER)
    public MessageConverter schedulesServiceMessageConverter() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean(SCHEDULES_SERVICE_CONNECTION_FACTORY)
    public ConnectionFactory schedulesServiceConnectionFactory(RabbitSchedulesServiceProperties properties) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setUsername(properties.username());
        connectionFactory.setPassword(properties.password());
        connectionFactory.setVirtualHost(properties.virtualHost());
        connectionFactory.setAddresses(properties.addresses());
        connectionFactory.setConnectionTimeout((int) properties.connectionTimeout().toMillis());
        return connectionFactory;
    }

    @Bean(SCHEDULES_SERVICE_RABBIT_TEMPLATE)
    public RabbitTemplate schedulesServiceRabbitTemplate(
            @Qualifier(SCHEDULES_SERVICE_CONNECTION_FACTORY) ConnectionFactory connectionFactory,
            @Qualifier(SCHEDULES_SERVICE_MESSAGE_CONVERTER) MessageConverter messageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setObservationEnabled(true);
        return rabbitTemplate;
    }

    @Bean(SCHEDULES_SERVICE_AMQP_ADMIN)
    public AmqpAdmin schedulesServiceAmqpAdmin(
            @Qualifier(SCHEDULES_SERVICE_RABBIT_TEMPLATE) RabbitTemplate rabbitTemplate
    ) {
        return new RabbitAdmin(rabbitTemplate);
    }

    @Bean(SCHEDULES_SERVICE_LISTENER)
    public SimpleRabbitListenerContainerFactory schedulesServiceListener(
            @Qualifier(SCHEDULES_SERVICE_CONNECTION_FACTORY) ConnectionFactory connectionFactory
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();

        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setDefaultRequeueRejected(false);
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean(SCHEDULES_SERVICE_BOOK_AMQP_MESSAGE_SENDER)
    public AmqpMessageSender schedulesServiceBookAmqpMessageSender(
            @Qualifier(SCHEDULES_SERVICE_CONNECTION_FACTORY) ConnectionFactory connectionFactory,
            RabbitScheduleServiceBookQueueingProperties queueProperties
    ) {
        return new RabbitScheduleServiceBookMessageSender(connectionFactory, queueProperties);
    }

    @Bean
    public MessageSender<ScheduleMessageType, ScheduleResponse> schedulesServiceMessageSender(
            @Qualifier(SCHEDULES_SERVICE_RABBIT_TEMPLATE) RabbitTemplate rabbitTemplate,
            Set<QueueingProperties> queueingProperties
    ) {
        return new RabbitScheduleServiceMessageSender(rabbitTemplate, queueingProperties);
    }

}
