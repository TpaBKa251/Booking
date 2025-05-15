package ru.tpu.hostel.booking.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.repository.BookingRepository;

import java.util.List;

/**
 * Класс для автоматического обновления статусов броней
 */
@Service
@RequiredArgsConstructor
public class BookingStateUpdater {

    private final BookingRepository bookingRepository;

    /**
     * Обновляет статус всех броней каждые 10 минут
     */
    @Scheduled(cron = "0 0/10 * * * ?", zone = "Asia/Tomsk")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateBookingStatuses() {
        List<Booking> bookings = bookingRepository.findAll();

        if (!bookings.isEmpty()) {
            for (Booking booking : bookings) {
                booking.getBookingState().updateStatus(booking, bookingRepository);
            }
        }
    }
}
