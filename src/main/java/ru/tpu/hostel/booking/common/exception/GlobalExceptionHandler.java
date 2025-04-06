package ru.tpu.hostel.booking.common.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений. Необходимо вручную прописывать только неожиданные/непредвиденные исключения
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper mapper;

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Map<String, String>> handleServiceException(ServiceException ex) {
        if (ex.getStatus().value() >= 500 && ex.getStatus().value() < 600) {
            log.error(ex.getMessage(), ex);
        }
        return getResponseEntity(ex.getStatus(), ex.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, String>> handleFeignException(FeignException ex) {
        if (ex.status() >= 500) {
            log.error(ex.contentUTF8(), ex);
        }
        if (ex.status() >= 200 && ex.status() < 600) {
            return getResponseEntity(HttpStatus.valueOf(ex.status()), mapHttpResponseErrorMessage(ex.contentUTF8()));
        }

        return getResponseEntity(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex
    ) {
        return getResponseEntity(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        return getResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return getResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    private ResponseEntity<Map<String, String>> getResponseEntity(HttpStatus status, String message) {
        Map<String, String> map = new HashMap<>();
        map.put("code", String.valueOf(status.value()));
        map.put("message", message);

        return new ResponseEntity<>(map, status);
    }

    private String mapHttpResponseErrorMessage(String message) {
        try {
            JsonNode json  = mapper.readTree(message);
            return json.get("message").asText();
        } catch (Exception e) {
            return message;
        }
    }
}
