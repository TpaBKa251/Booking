package ru.tpu.hostel.booking.rabbit.amqp;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.amqp.core.Message;

/**
 * Интерфейс отправителя сообщений в очередь RabbitMQ
 */
public interface AmqpMessageSender {

    /**
     * Отправляет сообщение асинхронно с указанным ID и контентом
     *
     * @param messageId ID сообщения
     * @param messagePayload контент сообщения
     * @throws JsonProcessingException если не удалось преобразовать контент в JSON
     */
    void send(String messageId, Object messagePayload) throws JsonProcessingException;

    /**
     * Отправляет сообщение асинхронно с указанным ID и контентом по ключу маршрутизации
     *
     * @param messageId ID сообщения
     * @param messagePayload контент сообщения
     * @param routingKey ключ маршрутизации для отправки
     * @throws JsonProcessingException если не удалось преобразовать контент в JSON
     */
    void send(String messageId, Object messagePayload, String routingKey) throws JsonProcessingException;

    /**
     * Отправляет сообщение асинхронно с указанным ID и контентом в виде JSON-строки
     *
     * @param messageId ID сообщения
     * @param jsonMessagePayload контент сообщения
     */
    void send(String messageId, String jsonMessagePayload);

    /**
     * Отправляет сообщение синхронно (RPC) с указанным ID и контентом и возвращает ответ
     *
     * @param messageId ID сообщения
     * @param messagePayload контент сообщения
     * @return ответ на отправленное сообщение
     * @throws JsonProcessingException если не удалось преобразовать контент в JSON
     */
    Message sendAndReceive(String messageId, Object messagePayload) throws JsonProcessingException;

}
