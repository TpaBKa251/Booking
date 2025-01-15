package ru.tpu.hostel.booking.exception;

public class ResponsibleNotFoundException extends NotFoundException {
    public ResponsibleNotFoundException(String message) {
        super(message);
    }

    public ResponsibleNotFoundException() {
        super("Ответственный не найден");
    }
}
