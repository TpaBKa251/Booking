package ru.tpu.hostel.booking.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static ru.tpu.hostel.booking.common.logging.Message.FINISH_REPOSITORY_METHOD_EXECUTION;
import static ru.tpu.hostel.booking.common.logging.Message.REPOSITORY_METHOD_EXECUTION_EXCEPTION;
import static ru.tpu.hostel.booking.common.logging.Message.START_REPOSITORY_METHOD_EXECUTION;
import static ru.tpu.hostel.booking.common.utils.TimeUtil.getLocalDateTimeStingFromMillis;

/**
 * Аспект для логирования репозиторных методов
 */
@Aspect
@Component
@Slf4j
public class RepositoryLoggingFilter {

    @Around("execution(public * ru.tpu.hostel.booking.repository.*Repository*.*(..))")
    public Object logRepositoryMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String repositoryName = Arrays.stream(joinPoint.getTarget().getClass().getInterfaces())
                .filter(i -> i.getPackageName().startsWith("ru.tpu.hostel.booking.repository"))
                .findFirst()
                .orElse(methodSignature.getDeclaringType())
                .getSimpleName();
        String methodName = methodSignature.getName();

        logStart(repositoryName, methodName);
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            logFinish(repositoryName, methodName, startTime);
            return result;
        } catch (Throwable throwable) {
            logException(repositoryName, methodName, startTime, throwable);
            throw throwable;
        }
    }

    private void logStart(String repositoryName, String methodName) {
        log.info(
                START_REPOSITORY_METHOD_EXECUTION,
                repositoryName,
                methodName
        );
    }

    private void logFinish(String repositoryName, String methodName, long startTime) {
        long finishTime = System.currentTimeMillis() - startTime;
        log.info(
                FINISH_REPOSITORY_METHOD_EXECUTION,
                repositoryName,
                methodName,
                finishTime
        );
    }

    private void logException(String repositoryName, String methodName, long startTimeMillis, Throwable throwable) {
        String startTime = getLocalDateTimeStingFromMillis(startTimeMillis);
        long executionTime = System.currentTimeMillis() - startTimeMillis;
        log.error(
                REPOSITORY_METHOD_EXECUTION_EXCEPTION,
                repositoryName,
                methodName,
                startTime,
                executionTime,
                throwable.getMessage(),
                throwable
        );
    }

}

