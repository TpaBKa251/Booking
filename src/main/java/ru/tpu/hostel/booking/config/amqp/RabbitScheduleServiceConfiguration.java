package ru.tpu.hostel.booking.config.amqp;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
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
import ru.tpu.hostel.booking.external.amqp.schedule.RabbitScheduleServiceMessageSender;

/**
 * Конфигурация брокера сообщений RabbitMQ для общения с микросервисом расписаний
 */
@Configuration
@Slf4j
@EnableConfigurationProperties({RabbitSchedulesServiceProperties.class, RabbitScheduleServiceQueueingProperties.class})
public class RabbitScheduleServiceConfiguration {

    public static final String SCHEDULES_SERVICE_LISTENER = "schedulesServiceListener";

    private static final String SCHEDULES_SERVICE_CONNECTION_FACTORY = "schedulesServiceConnectionFactory";

    private static final String SCHEDULES_SERVICE_AMQP_ADMIN = "schedulesServiceAmqpAdmin";

    private static final String SCHEDULES_SERVICE_RABBIT_TEMPLATE = "schedulesServiceRabbitTemplate";

    private static final String SCHEDULES_SERVICE_MESSAGE_CONVERTER = "schedulesServiceMessageConverter";

    private static final String SCHEDULES_SERVICE_AMQP_MESSAGE_SENDER = "schedulesServiceAmqpMessageSender";

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
            @Qualifier(SCHEDULES_SERVICE_RABBIT_TEMPLATE) RabbitTemplate rabbitTemplate,
            RabbitScheduleServiceQueueingProperties queueProperties
    ) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(rabbitTemplate);
        initQueue(rabbitAdmin, queueProperties);
        return rabbitAdmin;
    }

    private void initQueue(RabbitAdmin rabbitAdmin, RabbitScheduleServiceQueueingProperties queueProperties) {
        DirectExchange exchange = new DirectExchange(queueProperties.exchangeName());

        Queue queue = QueueBuilder.durable(queueProperties.queueReplyName())
                .quorum()
                .build();

        rabbitAdmin.declareQueue(queue);
        declareAndBindQueue(rabbitAdmin, queueProperties.replyRoutingKey(), exchange, queue);
    }

    private void declareAndBindQueue(
            RabbitAdmin rabbitAdmin,
            String replyRoutingKey,
            DirectExchange exchange,
            Queue queue
    ) {
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(replyRoutingKey);

        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareBinding(binding);
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

    @Bean(SCHEDULES_SERVICE_AMQP_MESSAGE_SENDER)
    public AmqpMessageSender schedulesServiceAmqpMessageSender(
            @Qualifier(SCHEDULES_SERVICE_RABBIT_TEMPLATE) RabbitTemplate rabbitTemplate,
            RabbitScheduleServiceQueueingProperties queueProperties
    ) {
        return new RabbitScheduleServiceMessageSender(rabbitTemplate, queueProperties);
    }

}
