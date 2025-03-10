package ru.tpu.hostel.booking.mapper;


import org.springframework.stereotype.Component;
import ru.tpu.hostel.booking.dto.response.BookingResponse;
import ru.tpu.hostel.booking.dto.response.BookingResponseWithUser;
import ru.tpu.hostel.booking.dto.response.BookingShortResponse;
import ru.tpu.hostel.booking.entity.BookingOld;

/**
 * Этот класс устарел и будет удалён в будущем.
 * Вместо него используйте {@link BookingMapper}.
 * @deprecated Класс заменён на {@link BookingMapper}.
 *
 * @see BookingMapper
 */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
@Component
public class BookingMapperOld {

    public static BookingShortResponse mapBookingToBookingShortResponseDto(BookingOld booking) {
        return new BookingShortResponse(
                booking.getStartTime(),
                booking.getEndTime()
        );
    }

    public static BookingResponse mapBookingToBookingResponseDto(BookingOld booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus(),
                booking.getType().getBookingTypeName()
        );
    }

    public static BookingResponseWithUser mapBookingToBookingResponseWithUserDto(BookingOld booking) {
        return new BookingResponseWithUser(
                booking.getId(),
                booking.getUser(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getStatus(),
                booking.getType().getBookingTypeName()
        );
    }
}
