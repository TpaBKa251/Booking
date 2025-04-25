package ru.tpu.hostel.booking.common.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static ru.tpu.hostel.booking.common.logging.Message.START_CONTROLLER_METHOD_EXECUTION;

/**
 * Аспект для логирования запросов от клиента
 */
@Aspect
@Component
@Slf4j
@Order(0)
public class RequestLoggingFilter {

    @Before("execution(public * ru.tpu.hostel.booking.controller.*Controller*.*(..))")
    public void logControllerMethod() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        if (request != null) {
            logRequest(request);
        }
    }

    private void logRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();

        log.info(START_CONTROLLER_METHOD_EXECUTION, method, requestURI);
    }

}

