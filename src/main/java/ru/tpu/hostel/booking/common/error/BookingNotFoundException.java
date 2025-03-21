package ru.tpu.hostel.booking.common.error;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String message) {
        super(message);
    }

    public BookingNotFoundException() {
        super("Бронь не найдена");
    }
}
