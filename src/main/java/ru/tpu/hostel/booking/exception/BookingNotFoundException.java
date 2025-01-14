package ru.tpu.hostel.booking.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String message) {
        super(message);
    }

    public BookingNotFoundException() {
        super("Бронь не найдена");
    }
}
