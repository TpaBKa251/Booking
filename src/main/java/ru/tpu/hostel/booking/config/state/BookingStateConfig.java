package ru.tpu.hostel.booking.config.state;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.service.state.BookingState;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Configuration
public class BookingStateConfig {

    @Bean
    public Map<BookingStatus, BookingState> bookingStates(List<BookingState> bookingStates) {
        Map<BookingStatus, BookingState> map = new EnumMap<>(BookingStatus.class);
        bookingStates.forEach(bookingState -> map.put(bookingState.getStatus(), bookingState));
        return map;
    }
}
