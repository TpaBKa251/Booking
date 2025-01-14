package ru.tpu.hostel.booking.service;

import ru.tpu.hostel.booking.dto.request.BookingTimeLineRequestDto;
import ru.tpu.hostel.booking.dto.request.BookingTimeSlotRequestDto;
import ru.tpu.hostel.booking.dto.response.BookingResponseDto;
import ru.tpu.hostel.booking.dto.response.BookingResponseWithUserDto;
import ru.tpu.hostel.booking.dto.response.BookingShortResponseDto;
import ru.tpu.hostel.booking.dto.response.TimeSlotResponseDto;
import ru.tpu.hostel.booking.enums.BookingStatus;
import ru.tpu.hostel.booking.enums.BookingType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BookingService {

    BookingResponseDto createBooking(BookingTimeLineRequestDto bookingTimeLineRequestDto, UUID userId);

    BookingResponseDto createBooking(BookingTimeSlotRequestDto bookingTimeSlotRequestDto, UUID userId);

    List<BookingShortResponseDto> getAvailableTimeBookings(LocalDate date, BookingType bookingType);

    List<TimeSlotResponseDto> getAvailableTimeBooking(LocalDate date, BookingType bookingType, UUID userId);

    BookingResponseDto cancelBooking(UUID bookingId, UUID userId);

    BookingResponseDto getBooking(UUID bookingId);

    List<BookingResponseDto> getBookingsByStatus(BookingStatus status, UUID userId);

    List<BookingResponseDto> getBookingsByUser(UUID userId);

    List<BookingResponseWithUserDto> getBookingsByTypeAndDate(BookingType bookingType, LocalDate date);

    List<BookingResponseWithUserDto> getBookingsByDate(LocalDate date);
}
