package ru.tpu.hostel.booking.config.amqp;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.MessagePropertiesBuilder;
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
import ru.tpu.hostel.booking.external.amqp.schedule.ScheduleMessageType;
import ru.tpu.hostel.internal.config.amqp.AmqpMessagingConfig;
import ru.tpu.hostel.internal.external.amqp.Microservice;

import java.util.Set;

/**
 * Конфигурация брокера сообщений RabbitMQ для общения с микросервисом расписаний
 */
@SuppressWarnings("NullableProblems")
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

    @Bean
    public AmqpMessagingConfig schedulesServiceAmqpMessagingConfigBook(
            @Qualifier(SCHEDULES_SERVICE_CONNECTION_FACTORY) ConnectionFactory connectionFactory,
            @Qualifier(SCHEDULES_SERVICE_MESSAGE_CONVERTER) MessageConverter messageConverter,
            RabbitScheduleServiceBookQueueingProperties properties
    ) {
        return new AmqpMessagingConfig() {
            @Override
            public RabbitTemplate rabbitTemplate() {
                RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
                rabbitTemplate.setMessageConverter(messageConverter);
                rabbitTemplate.setExchange(properties.exchangeName());
                rabbitTemplate.setRoutingKey(properties.routingKey());
                rabbitTemplate.setObservationEnabled(true);
                return rabbitTemplate;
            }

            @Override
            public MessageProperties messageProperties() {
                return MessagePropertiesBuilder.newInstance()
                        .setPriority(10)
                        .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .build();
            }

            @Override
            public Set<Microservice> receivingMicroservices() {
                return Set.of(Microservice.SCHEDULE);
            }

            @Override
            public boolean isApplicable(Enum<?> amqpMessageType) {
                return amqpMessageType == ScheduleMessageType.BOOK;
            }
        };
    }

    @Bean
    public AmqpMessagingConfig schedulesServiceAmqpMessagingConfigCancel(
            @Qualifier(SCHEDULES_SERVICE_CONNECTION_FACTORY) ConnectionFactory connectionFactory,
            @Qualifier(SCHEDULES_SERVICE_MESSAGE_CONVERTER) MessageConverter messageConverter,
            RabbitScheduleServiceCancelQueueingProperties properties
    ) {
        return new AmqpMessagingConfig() {
            @Override
            public RabbitTemplate rabbitTemplate() {
                RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
                rabbitTemplate.setMessageConverter(messageConverter);
                rabbitTemplate.setExchange(properties.exchangeName());
                rabbitTemplate.setRoutingKey(properties.routingKey());
                rabbitTemplate.setChannelTransacted(true);
                rabbitTemplate.setObservationEnabled(true);
                return rabbitTemplate;
            }

            @Override
            public MessageProperties messageProperties() {
                return MessagePropertiesBuilder.newInstance()
                        .setPriority(10)
                        .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .build();
            }

            @Override
            public Set<Microservice> receivingMicroservices() {
                return Set.of(Microservice.SCHEDULE);
            }

            @Override
            public boolean isApplicable(Enum<?> amqpMessageType) {
                return amqpMessageType == ScheduleMessageType.CANCEL;
            }
        };
    }

    @Bean
    public AmqpMessagingConfig schedulesServiceAmqpMessagingConfigCancelWithoutTransaction(
            @Qualifier(SCHEDULES_SERVICE_CONNECTION_FACTORY) ConnectionFactory connectionFactory,
            @Qualifier(SCHEDULES_SERVICE_MESSAGE_CONVERTER) MessageConverter messageConverter,
            RabbitScheduleServiceCancelQueueingProperties properties
    ) {
        return new AmqpMessagingConfig() {
            @Override
            public RabbitTemplate rabbitTemplate() {
                RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
                rabbitTemplate.setMessageConverter(messageConverter);
                rabbitTemplate.setExchange(properties.exchangeName());
                rabbitTemplate.setRoutingKey(properties.routingKey());
                rabbitTemplate.setObservationEnabled(true);
                return rabbitTemplate;
            }

            @Override
            public MessageProperties messageProperties() {
                return MessagePropertiesBuilder.newInstance()
                        .setPriority(10)
                        .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                        .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                        .build();
            }

            @Override
            public Set<Microservice> receivingMicroservices() {
                return Set.of(Microservice.SCHEDULE);
            }

            @Override
            public boolean isApplicable(Enum<?> amqpMessageType) {
                return amqpMessageType == ScheduleMessageType.CANCEL_WITHOUT_TRANSACTION;
            }
        };
    }

}
