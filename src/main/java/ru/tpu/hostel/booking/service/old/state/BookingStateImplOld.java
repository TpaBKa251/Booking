package ru.tpu.hostel.booking.service.old.state;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.common.logging.LogFilter;
import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.repository.BookingRepositoryOld;
import ru.tpu.hostel.booking.service.state.BookingStateUpdater;

import java.util.List;

/**
 * Этот класс устарел и будет удалён в будущем.
 * Вместо него используйте {@link BookingStateUpdater}.
 *
 * @see BookingStateUpdater
 * @deprecated Класс заменён на {@link BookingStateUpdater}.
 */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
@Service
@RequiredArgsConstructor
@EnableScheduling
public class BookingStateImplOld {

    private final BookingRepositoryOld bookingRepository;

    @Bean
    @LogFilter(enableResultLogging = false)
    public ApplicationRunner updateBookingStatusesOnStarts() {
        return args -> updateBookingStatuses();
    }

    @Scheduled(cron = "0 0/10 * * * ?", zone = "Asia/Tomsk")
    public void updateBookingStatuses() {
        List<BookingOld> bookings = bookingRepository.findAll();

        if (!bookings.isEmpty()) {
            for (BookingOld booking : bookings) {
                booking.getBookingState().updateStatus(booking, bookingRepository);
            }
        }
    }
}
