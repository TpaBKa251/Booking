package ru.tpu.hostel.booking.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.booking.service.state.BookingState;
import ru.tpu.hostel.internal.utils.TimeUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ru.tpu.hostel.booking.entity.BookingStatus.BOOKED;
import static ru.tpu.hostel.booking.entity.BookingStatus.IN_PROGRESS;

/**
 * Класс для автоматического обновления статусов броней
 */
@Service
@RequiredArgsConstructor
public class BookingStateUpdater {

    private final BookingRepository bookingRepository;

    private final Map<BookingStatus, BookingState> bookingStates;

    /**
     * Обновляет статус всех броней каждые 5 минут
     */
    @Scheduled(cron = "0 0/5 * * * ?", zone = "Asia/Tomsk")
    @Transactional
    public void updateBookingStatuses() {
        LocalDateTime now = TimeUtil.now();
        LocalDateTime dayStart = now.toLocalDate().atStartOfDay();
        List<Booking> bookings = bookingRepository.findAllOnDayForUpdateStatus(dayStart, now.plusMinutes(1));
        updateBookingStatuses(bookings);
    }

    @Transactional
    public void updateBookingStatusesOnStart() {
        List<Booking> bookings = bookingRepository.findAllByStatusIn(List.of(BOOKED, IN_PROGRESS));
        updateBookingStatuses(bookings);
    }

    private void updateBookingStatuses(List<Booking> bookings) {
        if (!bookings.isEmpty()) {
            for (Booking booking : bookings) {
                bookingStates.get(booking.getStatus()).updateStatus(booking);
            }
        }
    }
}
