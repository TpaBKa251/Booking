package ru.tpu.hostel.booking.service.impl.states;

import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.booking.service.state.BookingState;

@Service
public class CompletedState implements BookingState {

    @Override
    public void updateStatus(Booking booking, BookingRepository bookingRepository) {
    }

    @Override
    public void cancelBooking(Booking booking, BookingRepository bookingRepository) {
        throw new UnsupportedOperationException("Вы не можете отменить завершенную бронь");
    }
}
