package ru.tpu.hostel.booking.common.logging;

import lombok.experimental.UtilityClass;

/**
 * Сообщения для логов
 */
@UtilityClass
class Message {

    static final String START_REPOSITORY_METHOD_EXECUTION
            = "[REPOSITORY] Выполняется репозиторный метод {}.{}()";

    static final String FINISH_REPOSITORY_METHOD_EXECUTION
            = "[REPOSITORY] Завершилось выполнение репозиторного метода {}.{}(). Время выполнения: {} мс";

    static final String REPOSITORY_METHOD_EXECUTION_EXCEPTION
            = "[REPOSITORY] Ошибка во время выполнения репозиторного метода {}.{}(). "
            + "Время старта: {}, длительность: {} мс, ошибка: {}";

    static final String START_SERVICE_METHOD_EXECUTION = "[SERVICE] Выполняется сервисный метод {}.{}()";

    static final String START_SERVICE_METHOD_EXECUTION_WITH_PARAMETERS
            = "[SERVICE] Выполняется сервисный метод {}.{}({})";

    static final String FINISH_SERVICE_METHOD_EXECUTION
            = "[SERVICE] Завершилось выполнение сервисного метода {}.{}(). Время выполнения: {} мс";

    static final String FINISH_SERVICE_METHOD_EXECUTION_WITH_RESULT
            = "[SERVICE] Завершилось выполнение сервисного метода {}.{}() с результатом {}. Время выполнения: {} мс";

    static final String SERVICE_METHOD_EXECUTION_EXCEPTION
            = "[SERVICE] Ошибка во время выполнения сервисного метода {}.{}(). "
            + "Время старта: {}, длительность: {} мс, ошибка: {}";

    static final String START_CONTROLLER_METHOD_EXECUTION = "[REQUEST] {} {}";

    static final String FINISH_CONTROLLER_METHOD_EXECUTION = "[RESPONSE] Статус: {}. Время выполнения: {} мс";

    static final String START_RABBIT_SENDING_METHOD_EXECUTION = "[RABBIT] Отправка сообщения: messageId={}, payload={}";

    static final String START_RABBIT_SENDING_METHOD_VIA_ROUTING_KEY_EXECUTION
            = "[RABBIT] Отправка сообщения по ключу {}: messageId={}, payload={}";

    static final String START_RABBIT_SENDING_METHOD_VIA_RPC_EXECUTION
            = "[RABBIT] Отправка RPC сообщения: messageId={}, payload={}";

    static final String FINISH_RABBIT_SENDING_METHOD_EXECUTION
            = "[RABBIT] Сообщение отправлено: messageId={}, playload={}. Время выполнения {} мс";

    static final String FINISH_RABBIT_RECEIVING_RPC
            = "[RABBIT] Получен RPC ответ: messageId={}, playload={}. Время выполнения {} мс";

    static final String RABBIT_SENDING_METHOD_EXECUTION_EXCEPTION = "[RABBIT] Ошибка отправки сообщения: "
            + "messageId={}, payload={}. Ошибка: {}. Время начала отправки: {}, время выполнения: {} мс";

    static final String RABBIT_RECEIVING_RPC_EXCEPTION = "[RABBIT] Ошибка получения ответа на сообщение: "
            + "messageId={}, payload={}. Ошибка: {}. Время начала отправки: {}, время выполнения: {} мс";

    static final String FINISH_RABBIT_SENDING_METHOD_VIA_RPC_EXECUTION_WITH_EMPTY_RESPONSE = "[RABBIT] Пустой ответ "
            + "на сообщение: messageId={}, payload={}. Время начала отправки: {}, время выполнения: {} мс";
}
