package ru.tpu.hostel.booking.service.old.state;

import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.repository.BookingRepositoryOld;
import ru.tpu.hostel.booking.service.state.impl.CancelState;
import ru.tpu.hostel.internal.exception.ServiceException;

/**
 * Этот класс устарел и будет удалён в будущем.
 * Вместо него используйте {@link CancelState}.
 *
 * @see CancelState
 * @deprecated Класс заменён на {@link CancelState}.
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
        throw new ServiceException.UnprocessableEntity("Вы не можете закрыть уже закрытую бронь");
    }
}
