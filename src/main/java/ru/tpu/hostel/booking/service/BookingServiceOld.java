package ru.tpu.hostel.booking.service;

import ru.tpu.hostel.booking.dto.request.BookingTimeLineRequest;
import ru.tpu.hostel.booking.dto.request.BookingTimeSlotRequest;
import ru.tpu.hostel.booking.dto.response.BookingResponse;
import ru.tpu.hostel.booking.dto.response.BookingResponseWithUser;
import ru.tpu.hostel.booking.dto.response.BookingShortResponse;
import ru.tpu.hostel.booking.dto.response.TimeSlotResponse;
import ru.tpu.hostel.booking.enums.BookingStatus;
import ru.tpu.hostel.booking.enums.BookingType;
import ru.tpu.hostel.booking.repository.BookingRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Этот класс устарел и будет удалён в будущем.
 * Вместо него используйте {@link BookingService}.
 * @deprecated Класс заменён на {@link BookingService}.
 *
 * @see BookingService
 */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
public interface BookingServiceOld {

    BookingResponse createBooking(BookingTimeLineRequest bookingTimeLineRequestDto, UUID userId);

    BookingResponse createBooking(BookingTimeSlotRequest bookingTimeSlotRequestDto, UUID userId);

    List<BookingShortResponse> getAvailableTimeBookings(LocalDate date, BookingType bookingType);

    List<TimeSlotResponse> getAvailableTimeBooking(LocalDate date, BookingType bookingType, UUID userId);

    BookingResponse cancelBooking(UUID bookingId, UUID userId);

    BookingResponse getBooking(UUID bookingId);

    List<BookingResponse> getBookingsByStatus(BookingStatus status, UUID userId);

    List<BookingResponse> getBookingsByUser(UUID userId);

    List<BookingResponseWithUser> getBookingsByTypeAndDate(BookingType bookingType, LocalDate date);

    List<BookingResponseWithUser> getBookingsByDate(LocalDate date);
}
