package ru.tpu.hostel.booking.service.impl.states;

import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.enums.BookingStatus;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.booking.repository.BookingRepositoryOld;
import ru.tpu.hostel.booking.service.state.BookingState;
import ru.tpu.hostel.booking.utils.TimeNow;

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
public class BookedStateOld implements BookingState {

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