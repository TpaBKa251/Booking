package ru.tpu.hostel.booking.service.impl.states;

import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.enums.BookingStatus;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.booking.service.state.BookingState;

import java.time.LocalDateTime;

@Service
public class BookedState implements BookingState {

    @Override
    public void updateStatus(Booking booking, BookingRepository bookingRepository) {
        if (booking.getStartTime().isBefore(LocalDateTime.now())) {
            booking.setStatus(BookingStatus.IN_PROGRESS);
            bookingRepository.save(booking);
        }
    }

    @Override
    public void cancelBooking(Booking booking, BookingRepository bookingRepository) {
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }
}