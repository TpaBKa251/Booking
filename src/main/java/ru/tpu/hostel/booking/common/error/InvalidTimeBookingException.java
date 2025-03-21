package ru.tpu.hostel.booking.common.error;

public class InvalidTimeBookingException extends RuntimeException {
    public InvalidTimeBookingException(String message) {
        super(message);
    }
}
