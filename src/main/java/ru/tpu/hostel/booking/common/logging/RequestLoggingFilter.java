package ru.tpu.hostel.booking.common.logging;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
public class RequestLoggingFilter {

    @Around("execution(public * ru.tpu.hostel.booking.controller.*Controller*.*(..))")
    public Object logControllerMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        if (request != null) {
            logRequest(request);
        }

        return joinPoint.proceed();
    }

    private void logRequest(HttpServletRequest request) {
        String method = request.getMethod();
        String requestURI = request.getRequestURI();

        log.info(START_CONTROLLER_METHOD_EXECUTION, method, requestURI);
    }

}

