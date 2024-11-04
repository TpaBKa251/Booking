package ru.tpu.hostel.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.repository.BookingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class BookingStateImpl {

    private final BookingRepository bookingRepository;

    @Bean
    public ApplicationRunner updateBookingStatusesOnStart() {
        return args -> updateBookingStatuses();
    }

    @Scheduled(cron = "0 0 6-19 * * ?", zone = "Asia/Tomsk")
    @Scheduled(cron = "0 0/10 20-23,0-0 * * ?", zone = "Asia/Tomsk")
    public void updateBookingStatuses() {
        List<Booking> bookings = bookingRepository.findAll();

        if (!bookings.isEmpty()) {
            for (Booking booking : bookings) {
                booking.getBookingState().updateStatus(booking, bookingRepository);
            }
        }
    }
}
