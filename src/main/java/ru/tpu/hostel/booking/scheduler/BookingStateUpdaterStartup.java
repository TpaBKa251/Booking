package ru.tpu.hostel.booking.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tpu.hostel.internal.common.logging.LogFilter;

@Configuration
@RequiredArgsConstructor
@LogFilter(enableMethodLogging = false)
public class BookingStateUpdaterStartup {

    private final BookingStateUpdater bookingStateUpdater;

    @Bean
    @LogFilter(enableMethodLogging = false)
    public ApplicationRunner updateBookingStatusesOnStart() {
        return args -> bookingStateUpdater.updateBookingStatuses();
    }
}
