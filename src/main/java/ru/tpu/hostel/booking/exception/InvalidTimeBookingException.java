package ru.tpu.hostel.booking.exception;

public class InvalidTimeBookingException extends RuntimeException {
    public InvalidTimeBookingException(String message) {
        super(message);
    }
}
