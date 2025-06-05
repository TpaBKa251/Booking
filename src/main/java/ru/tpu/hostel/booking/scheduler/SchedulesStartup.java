package ru.tpu.hostel.booking.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tpu.hostel.internal.common.logging.LogFilter;

@Configuration
@RequiredArgsConstructor
@LogFilter(enableMethodLogging = false)
public class SchedulesStartup {

    private final BookingStateUpdater bookingStateUpdater;

    private final CacheCleaner cacheCleaner;

    @Bean
    @LogFilter(enableMethodLogging = false)
    public ApplicationRunner updateBookingStatusesOnStart() {
        return args -> {
            bookingStateUpdater.updateBookingStatusesOnStart();
            cacheCleaner.cleanCache();
        };
    }
}
