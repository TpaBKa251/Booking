package ru.tpu.hostel.booking.service.state.impl;

import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.common.exception.ServiceException;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.booking.service.state.BookingState;

/**
 * Реализация интерфейса {@link BookingState} для состояния "Закрыто"
 */
@Service
public class CancelState implements BookingState {

    @Override
    public void updateStatus(Booking booking, BookingRepository bookingRepository) {
        // не нужно реализовывать
    }

    @Override
    public void cancelBooking(Booking booking, BookingRepository bookingRepository) {
        throw new ServiceException.UnprocessableEntity("Вы не можете закрыть уже закрытую бронь");
    }
}
