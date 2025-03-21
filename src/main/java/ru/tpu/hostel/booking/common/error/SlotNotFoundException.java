package ru.tpu.hostel.booking.common.error;

public class SlotNotFoundException extends RuntimeException {

    public SlotNotFoundException(String message) {
        super(message);
    }

    public SlotNotFoundException() {
        super("Слот не найден");
    }
}
