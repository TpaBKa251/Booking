package ru.tpu.hostel.booking.service.impl.states;

import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.repository.BookingRepositoryOld;
import ru.tpu.hostel.booking.service.state.BookingState;

/**
 * Этот класс устарел и будет удалён в будущем.
 * Вместо него используйте {@link NotBookedState}.
 * @deprecated Класс заменён на {@link NotBookedState}.
 *
 * @see NotBookedState
 */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
@Service
public class NotBookedStateOld implements BookingState {
    @Override
    public void updateStatus(BookingOld booking, BookingRepositoryOld bookingRepository) {
    }

    @Override
    public void cancelBooking(BookingOld booking, BookingRepositoryOld bookingRepository) {
        throw new UnsupportedOperationException("Вы не можете отменить незабронированную бронь");
    }
}
