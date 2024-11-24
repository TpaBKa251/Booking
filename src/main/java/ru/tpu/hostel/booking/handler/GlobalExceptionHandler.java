package ru.tpu.hostel.booking.handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.tpu.hostel.booking.exception.BookingNotFoundException;
import ru.tpu.hostel.booking.exception.InvalidTimeBookingException;
import ru.tpu.hostel.booking.exception.SlotAlreadyBookedException;
import ru.tpu.hostel.booking.exception.SlotNotFoundException;
import ru.tpu.hostel.booking.exception.UserNotFound;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();

        errorResponse.put("code", "400");
        errorResponse.put("message", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleBookingNotFoundException(BookingNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();

        errorResponse.put("code", "404");
        errorResponse.put("message", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<Map<String, String>> handleBookingNotFoundException(UserNotFound ex) {
        Map<String, String> errorResponse = new HashMap<>();

        errorResponse.put("code", "404");
        errorResponse.put("message", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SlotNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleSlotNotFoundException(SlotNotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();

        errorResponse.put("code", "404");
        errorResponse.put("message", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SlotAlreadyBookedException.class)
    public ResponseEntity<Map<String, String>> handleSlotAlreadyBookedException(SlotAlreadyBookedException ex) {
        Map<String, String> errorResponse = new HashMap<>();

        errorResponse.put("code", "400");
        errorResponse.put("message", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTimeBookingException.class)
    public ResponseEntity<Map<String, String>> handleInvalidTimeBookingException(InvalidTimeBookingException ex) {
        Map<String, String> errorResponse = new HashMap<>();

        errorResponse.put("code", "400");
        errorResponse.put("message", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Map<String, String>> handleUnsupportedOperationException(UnsupportedOperationException ex) {
        Map<String, String> errorResponse = new HashMap<>();

        errorResponse.put("code", "422");
        errorResponse.put("message", ex.getMessage());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", "409");
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", "400");
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        Map<String, String> map = new HashMap<>();

        map.put("code", "500");
        map.put("message", ex.getMessage());

        return new ResponseEntity<>(map, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
