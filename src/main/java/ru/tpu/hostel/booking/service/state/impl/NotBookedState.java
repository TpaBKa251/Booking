package ru.tpu.hostel.booking.service.state.impl;

import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.service.state.BookingState;
import ru.tpu.hostel.internal.exception.ServiceException;

/**
 * Реализация интерфейса {@link BookingState} для состояния "Не забронировано"
 */
@Service
public class NotBookedState implements BookingState {

    @Override
    public void updateStatus(Booking booking) {
        // не нужно реализовывать
    }

    @Override
    public void cancelBooking(Booking booking) {
        throw new ServiceException.Conflict("Вы не можете отменить незабронированную бронь");
    }

    @Override
    public BookingStatus getStatus() {
        return BookingStatus.NOT_BOOKED;
    }
}
