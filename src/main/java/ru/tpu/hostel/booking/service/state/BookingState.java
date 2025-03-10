package ru.tpu.hostel.booking.service.state;

import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.repository.BookingRepositoryOld;

public interface BookingState {

    void updateStatus(BookingOld booking, BookingRepositoryOld bookingRepository);

    void cancelBooking(BookingOld booking, BookingRepositoryOld bookingRepository);
}
