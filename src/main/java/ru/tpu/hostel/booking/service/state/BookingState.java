package ru.tpu.hostel.booking.service.state;

import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.repository.BookingRepository;

public interface BookingState {

    void updateStatus(Booking booking, BookingRepository bookingRepository);

    void cancelBooking(Booking booking, BookingRepository bookingRepository);
}
