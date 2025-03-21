package ru.tpu.hostel.booking.service.old.state;

import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.repository.BookingRepositoryOld;
import ru.tpu.hostel.booking.common.utils.TimeNow;

/**
 * Этот класс устарел и будет удалён в будущем.
 * Вместо него используйте {@link BookedState}.
 * @deprecated Класс заменён на {@link BookedState}.
 *
 * @see BookedState
 */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
@Service
public class BookedStateOld implements BookingStateOld {

    @Override
    public void updateStatus(BookingOld booking, BookingRepositoryOld bookingRepository) {
        if (booking.getStartTime().isBefore(TimeNow.now())) {
            booking.setStatus(BookingStatus.IN_PROGRESS);
            bookingRepository.save(booking);
        }
    }

    @Override
    public void cancelBooking(BookingOld booking, BookingRepositoryOld bookingRepository) {
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }
}