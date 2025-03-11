package ru.tpu.hostel.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.booking.dto.request.BookingTimeLineRequest;
import ru.tpu.hostel.booking.dto.request.BookingTimeSlotRequest;
import ru.tpu.hostel.booking.dto.response.BookingResponse;
import ru.tpu.hostel.booking.dto.response.BookingResponseWithUser;
import ru.tpu.hostel.booking.dto.response.BookingShortResponse;
import ru.tpu.hostel.booking.dto.response.TimeSlotResponse;
import ru.tpu.hostel.booking.enums.BookingStatus;
import ru.tpu.hostel.booking.enums.BookingType;
import ru.tpu.hostel.booking.service.BookingServiceOld;
import ru.tpu.hostel.booking.service.impl.BookingStateImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Этот класс устарел и будет удалён в будущем.
 * Вместо него используйте {@link BookingController}.
 * @deprecated Класс заменён на {@link BookingController}.
 *
 * @see BookingController
 */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
@RestController
@RequestMapping("bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingControllerOld {

    private final BookingServiceOld bookingService;

    /**
     * @deprecated убрана возможность брони по таймлайну
     */
    @Deprecated
    @PostMapping("/timeline/{userId}")
    public BookingResponse book(@RequestBody @Valid BookingTimeLineRequest bookingTimeLineRequestDto, @PathVariable UUID userId) {
        log.info(LocalDateTime.now().toString());

        return bookingService.createBooking(bookingTimeLineRequestDto, userId);
    }

    @PostMapping("/timeslot/{userId}")
    public BookingResponse book(@RequestBody @Valid BookingTimeSlotRequest bookingTimeSlotRequestDto, @PathVariable UUID userId) {
        return bookingService.createBooking(bookingTimeSlotRequestDto, userId);
    }

    /**
     * @deprecated убрана возможность брони по таймлайну
     */
    @Deprecated
    @GetMapping("/available/timeline/{date}/{bookingType}")
    public List<BookingShortResponse> getAvailableTimeBookings(@PathVariable LocalDate date, @PathVariable BookingType bookingType) {
        return bookingService.getAvailableTimeBookings(date, bookingType);
    }

    @GetMapping("/available/timeslot/{date}/{bookingType}/{userId}")
    public List<TimeSlotResponse> getAvailableTimeBooking(@PathVariable LocalDate date, @PathVariable BookingType bookingType, @PathVariable UUID userId) {
        return bookingService.getAvailableTimeBooking(date, bookingType, userId);
    }

    @PatchMapping("/cancel/{bookingId}/{userId}")
    public BookingResponse cancel(@PathVariable UUID bookingId, @PathVariable UUID userId) {
        return bookingService.cancelBooking(bookingId, userId);
    }

    @GetMapping("/get/all/by/status/user/{status}/{userId}")
    public List<BookingResponse> getAllByStatus(@PathVariable BookingStatus status, @PathVariable UUID userId) {
        return bookingService.getBookingsByStatus(status, userId);
    }

    @GetMapping("/get/all/by/user/{userId}")
    public List<BookingResponse> getAllByUserId(@PathVariable UUID userId) {
        return bookingService.getBookingsByUser(userId);
    }

    @GetMapping("/get/all/by/type/date/{type}/{date}")
    public List<BookingResponseWithUser> getAllByTypeAndDate(@PathVariable BookingType type, @PathVariable LocalDate date) {
        return bookingService.getBookingsByTypeAndDate(type, date);
    }

    @GetMapping("/get/all/by/date/{date}")
    public List<BookingResponseWithUser> getAllByDate(@PathVariable LocalDate date) {
        return bookingService.getBookingsByDate(date);
    }
}
