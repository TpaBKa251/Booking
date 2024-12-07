package ru.tpu.hostel.booking.mapper;

import org.springframework.stereotype.Component;
import ru.tpu.hostel.booking.dto.response.BookingResponseDto;
import ru.tpu.hostel.booking.dto.response.BookingShortResponseDto;
import ru.tpu.hostel.booking.entity.Booking;

@Component
public class BookingMapper {

    public static BookingShortResponseDto mapBookingToBookingShortResponseDto(Booking booking) {
        return new BookingShortResponseDto(
                booking.getStartTime(),
                booking.getEndTime()
        );
    }

    public static BookingResponseDto mapBookingToBookingResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus(),
                booking.getType().getBookingTypeName()
        );
    }
}
