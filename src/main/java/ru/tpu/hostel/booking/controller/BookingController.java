package ru.tpu.hostel.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.booking.dto.request.BookingTimeLineRequestDto;
import ru.tpu.hostel.booking.dto.request.BookingTimeSlotRequestDto;
import ru.tpu.hostel.booking.dto.response.BookingResponseDto;
import ru.tpu.hostel.booking.dto.response.BookingShortResponseDto;
import ru.tpu.hostel.booking.dto.response.TimeSlotResponseDto;
import ru.tpu.hostel.booking.enums.BookingStatus;
import ru.tpu.hostel.booking.enums.BookingType;
import ru.tpu.hostel.booking.service.BookingService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/timeline/{userId}")
    public BookingResponseDto book(@RequestBody @Valid BookingTimeLineRequestDto bookingTimeLineRequestDto, @PathVariable UUID userId) {
        return bookingService.createBooking(bookingTimeLineRequestDto, userId);
    }

    @PostMapping("/timeslot/{userId}")
    public BookingResponseDto book(@RequestBody @Valid BookingTimeSlotRequestDto bookingTimeSlotRequestDto, @PathVariable UUID userId) {
        return bookingService.createBooking(bookingTimeSlotRequestDto, userId);
    }

    @GetMapping("/available/timeline/{date}/{bookingType}")
    public List<BookingShortResponseDto> getAvailableTimeBookings(@PathVariable LocalDate date, @PathVariable BookingType bookingType) {
        return bookingService.getAvailableTimeBookings(date, bookingType);
    }

    @GetMapping("/available/timeslot/{date}/{bookingType}")
    public List<TimeSlotResponseDto> getAvailableTimeBooking(@PathVariable LocalDate date, @PathVariable BookingType bookingType) {
        return bookingService.getAvailableTimeBooking(date, bookingType);
    }

    @PatchMapping("/cancel/{bookingId}/{userId}")
    public BookingResponseDto cancel(@PathVariable UUID bookingId, @PathVariable UUID userId) {
        return bookingService.cancelBooking(bookingId, userId);
    }

    @GetMapping("/get/all/{status}/{userId}")
    public List<BookingResponseDto> getAllByStatus(@PathVariable BookingStatus status, @PathVariable UUID userId) {
        return bookingService.getBookingsByStatus(status, userId);
    }

    @GetMapping("/get/all/{userId}")
    public List<BookingResponseDto> getAllByUserId(@PathVariable UUID userId) {
        return bookingService.getBookingsByUser(userId);
    }
}
