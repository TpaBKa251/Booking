package ru.tpu.hostel.booking.service.impl.states;

import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.repository.BookingRepositoryOld;
import ru.tpu.hostel.booking.service.state.BookingState;

/**
 * Этот класс устарел и будет удалён в будущем.
 * Вместо него используйте {@link CompletedState}.
 * @deprecated Класс заменён на {@link CompletedState}.
 *
 * @see CompletedState
 */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
@Service
public class CompletedStateOld implements BookingState {

    @Override
    public void updateStatus(BookingOld booking, BookingRepositoryOld bookingRepository) {
    }

    @Override
    public void cancelBooking(BookingOld booking, BookingRepositoryOld bookingRepository) {
        throw new UnsupportedOperationException("Вы не можете отменить завершенную бронь");
    }
}
