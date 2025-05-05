package ru.tpu.hostel.booking.service.old.state;

import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.repository.BookingRepositoryOld;
import ru.tpu.hostel.booking.service.state.impl.CompletedState;
import ru.tpu.hostel.internal.exception.ServiceException;

/**
 * Этот класс устарел и будет удалён в будущем.
 * Вместо него используйте {@link CompletedState}.
 *
 * @see CompletedState
 * @deprecated Класс заменён на {@link CompletedState}.
 */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
@Service
public class CompletedStateOld implements BookingStateOld {

    @Override
    public void updateStatus(BookingOld booking, BookingRepositoryOld bookingRepository) {
    }

    @Override
    public void cancelBooking(BookingOld booking, BookingRepositoryOld bookingRepository) {
        throw new ServiceException.UnprocessableEntity("Вы не можете отменить завершенную бронь");
    }
}
