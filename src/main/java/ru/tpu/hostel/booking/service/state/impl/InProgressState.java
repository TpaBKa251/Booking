package ru.tpu.hostel.booking.service.state.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.service.state.BookingState;
import ru.tpu.hostel.internal.exception.ServiceException;
import ru.tpu.hostel.internal.utils.TimeUtil;

import static ru.tpu.hostel.booking.entity.BookingStatus.IN_PROGRESS;

/**
 * Реализация интерфейса {@link BookingState} для состояния "В процессе"
 */
@Service
@RequiredArgsConstructor
public class InProgressState implements BookingState {

    @Override
    public void updateStatus(Booking booking) {
        if (booking.getEndTime().isBefore(TimeUtil.now())) {
            booking.setStatus(BookingStatus.COMPLETED);
        }
    }

    @Override
    public void cancelBooking(Booking booking) {
        throw new ServiceException.Conflict("Вы не можете отменить уже начатую бронь");
    }

    @Override
    public BookingStatus getStatus() {
        return IN_PROGRESS;
    }
}
