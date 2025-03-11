package ru.tpu.hostel.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.repository.BookingRepositoryOld;

import java.util.List;

/**
 * Этот класс устарел и будет удалён в будущем.
 * Вместо него используйте {@link BookingStateImpl}.
 * @deprecated Класс заменён на {@link BookingStateImpl}.
 *
 * @see BookingStateImpl
 */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
@Service
@RequiredArgsConstructor
@EnableScheduling
public class BookingStateImplOld {

    private final BookingRepositoryOld bookingRepository;

    @Bean
    public ApplicationRunner updateBookingStatusesOnStart() {
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
