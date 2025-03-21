package ru.tpu.hostel.booking.common.error;

public class AccessException extends RuntimeException {

    public AccessException(String message) {
        super(message);
    }

    public AccessException() {
        super("Доступ запрещен");
    }

}
