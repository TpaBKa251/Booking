package ru.tpu.hostel.booking.service.impl.states;

import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.booking.repository.BookingRepositoryOld;
import ru.tpu.hostel.booking.service.state.BookingState;

/**
 * Этот класс устарел и будет удалён в будущем.
 * Вместо него используйте {@link CancelState}.
 * @deprecated Класс заменён на {@link CancelState}.
 *
 * @see CancelState
 */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
@Service
public class CancelStateOld implements BookingState {
    @Override
    public void updateStatus(BookingOld booking, BookingRepositoryOld bookingRepository) {
    }

    @Override
    public void cancelBooking(BookingOld booking, BookingRepositoryOld bookingRepository) {
        throw new UnsupportedOperationException("Вы не можете закрыть уже закрытую бронь");
    }
}
