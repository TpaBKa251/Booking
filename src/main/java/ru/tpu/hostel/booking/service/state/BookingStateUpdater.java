package ru.tpu.hostel.booking.service.state;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.common.logging.LogFilter;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.repository.BookingRepository;

import java.util.List;

/**
 * Класс для автоматического обновления статусов броней
 */
@Service
@RequiredArgsConstructor
@EnableScheduling
public class BookingStateUpdater {

    private final BookingRepository bookingRepository;

    /**
     * Бин для вызова метода на старте приложения
     *
     * @return хэзик че возвращает :)
     */
    @Bean
    @LogFilter(enableResultLogging = false)
    public ApplicationRunner updateBookingStatusesOnStart() {
        return args -> updateBookingStatuses();
    }

    /**
     * Обновляет статус всех броней каждые 10 минут
     */
    @Scheduled(cron = "0 0/10 * * * ?", zone = "Asia/Tomsk")
    public void updateBookingStatuses() {
        List<Booking> bookings = bookingRepository.findAll();

        if (!bookings.isEmpty()) {
            for (Booking booking : bookings) {
                booking.getBookingState().updateStatus(booking, bookingRepository);
            }
        }
    }
}
