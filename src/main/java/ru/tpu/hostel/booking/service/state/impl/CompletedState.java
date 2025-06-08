package ru.tpu.hostel.booking.service.state.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.booking.service.state.BookingState;
import ru.tpu.hostel.internal.exception.ServiceException;
import ru.tpu.hostel.internal.utils.TimeUtil;

import java.time.Duration;

/**
 * Реализация интерфейса {@link BookingState} для состояния "Завершено"
 */
@Service
@RequiredArgsConstructor
public class CompletedState implements BookingState {

    private final BookingRepository bookingRepository;

    @Override
    public void updateStatus(Booking booking) {
        long daysBetween = Duration.between(booking.getStartTime(), TimeUtil.now()).toDays();
        if (daysBetween >= 30) {
            bookingRepository.delete(booking);
        }
    }

    @Override
    public void cancelBooking(Booking booking) {
        throw new ServiceException.Conflict("Вы не можете отменить завершенную бронь");
    }

    @Override
    public BookingStatus getStatus() {
        return BookingStatus.COMPLETED;
    }
}
