package ru.tpu.hostel.booking.service.state.impl;

import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.booking.service.state.BookingState;
import ru.tpu.hostel.internal.exception.ServiceException;

/**
 * Реализация интерфейса {@link BookingState} для состояния "Не забронировано"
 */
@Service
public class NotBookedState implements BookingState {

    @Override
    public void updateStatus(Booking booking, BookingRepository bookingRepository) {
        // не нужно реализовывать
    }

    @Override
    public void cancelBooking(Booking booking, BookingRepository bookingRepository) {
        throw new ServiceException.UnprocessableEntity("Вы не можете отменить незабронированную бронь");
    }
}
