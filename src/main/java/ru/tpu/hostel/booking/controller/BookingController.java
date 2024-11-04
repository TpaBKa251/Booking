package ru.tpu.hostel.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.booking.dto.request.BookingTimeLineRequestDto;
import ru.tpu.hostel.booking.dto.request.BookingTimeSlotRequestDto;
import ru.tpu.hostel.booking.dto.response.BookingResponseDto;
import ru.tpu.hostel.booking.dto.response.BookingShortResponseDto;
import ru.tpu.hostel.booking.dto.response.TimeSlotResponseDto;
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

    @PostMapping("/timeline")
    public BookingResponseDto book(BookingTimeLineRequestDto bookingTimeLineRequestDto, UUID userId) {
        return bookingService.createBooking(bookingTimeLineRequestDto, userId);
    }

    @PostMapping("/timeslot")
    public BookingResponseDto book(BookingTimeSlotRequestDto bookingTimeSlotRequestDto, UUID userId) {
        return bookingService.createBooking(bookingTimeSlotRequestDto, userId);
    }

    @GetMapping("/available/timeline")
    public List<BookingShortResponseDto> getAvailableTimeBookings(LocalDate date, BookingType bookingType) {
        return bookingService.getAvailableTimeBookings(date, bookingType);
    }

    @GetMapping("/available/timeslot")
    public List<TimeSlotResponseDto> getAvailableTimeBooking(LocalDate date, BookingType bookingType) {
        return bookingService.getAvailableTimeBooking(date, bookingType);
    }

    @PatchMapping("/cancel")
    public BookingResponseDto cancel(UUID bookingId, UUID userId) {
        return bookingService.cancelBooking(bookingId, userId);
    }
}
