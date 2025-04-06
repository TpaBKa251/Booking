package ru.tpu.hostel.booking.service.old.state;

import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.repository.BookingRepositoryOld;

@Deprecated(forRemoval = true)
public interface BookingStateOld {

    void updateStatus(BookingOld booking, BookingRepositoryOld bookingRepository);

    void cancelBooking(BookingOld booking, BookingRepositoryOld bookingRepository);
}
