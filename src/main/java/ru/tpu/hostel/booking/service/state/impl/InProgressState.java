package ru.tpu.hostel.booking.service.state.impl;

import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.common.exception.ServiceException;
import ru.tpu.hostel.booking.common.utils.TimeUtil;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.booking.service.state.BookingState;

/**
 * Реализация интерфейса {@link BookingState} для состояния "В процессе"
 */
@Service
public class InProgressState implements BookingState {

    @Override
    public void updateStatus(Booking booking, BookingRepository bookingRepository) {
        if (booking.getEndTime().isBefore(TimeUtil.now())) {
            booking.setStatus(BookingStatus.COMPLETED);
            bookingRepository.save(booking);

            // TODO: отправить уведомление, что бронь завершилась
        }
    }

    @Override
    public void cancelBooking(Booking booking, BookingRepository bookingRepository) {
        throw new ServiceException.UnprocessableEntity("Вы не можете отменить уже начатую бронь");
    }
}
