package ru.tpu.hostel.booking.service.old.state;

import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.repository.BookingRepositoryOld;

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
public class CancelStateOld implements BookingStateOld {
    @Override
    public void updateStatus(BookingOld booking, BookingRepositoryOld bookingRepository) {
    }

    @Override
    public void cancelBooking(BookingOld booking, BookingRepositoryOld bookingRepository) {
        throw new UnsupportedOperationException("Вы не можете закрыть уже закрытую бронь");
    }
}
