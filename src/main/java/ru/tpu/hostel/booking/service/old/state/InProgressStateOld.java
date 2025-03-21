package ru.tpu.hostel.booking.service.old.state;

import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.repository.BookingRepositoryOld;
import ru.tpu.hostel.booking.common.utils.TimeNow;

/**
 * Этот класс устарел и будет удалён в будущем.
 * Вместо него используйте {@link InProgressState}.
 * @deprecated Класс заменён на {@link InProgressState}.
 *
 * @see InProgressState
 */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
@Service
public class InProgressStateOld implements BookingStateOld {

    @Override
    public void updateStatus(BookingOld booking, BookingRepositoryOld bookingRepository) {
        if (booking.getEndTime().isBefore(TimeNow.now())) {
            booking.setStatus(BookingStatus.COMPLETED);
            bookingRepository.save(booking);
        }
    }

    @Override
    public void cancelBooking(BookingOld booking, BookingRepositoryOld bookingRepository) {
        throw new UnsupportedOperationException("Вы не можете отменить уже начатую бронь");
    }
}
