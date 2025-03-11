package ru.tpu.hostel.booking.service.impl;

import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.dto.request.BookingTimeLineRequest;
import ru.tpu.hostel.booking.dto.request.BookingTimeSlotRequest;
import ru.tpu.hostel.booking.dto.response.BookingResponse;
import ru.tpu.hostel.booking.dto.response.BookingResponseWithUser;
import ru.tpu.hostel.booking.dto.response.BookingShortResponse;
import ru.tpu.hostel.booking.dto.response.TimeSlotResponse;
import ru.tpu.hostel.booking.enums.BookingStatus;
import ru.tpu.hostel.booking.enums.BookingType;
import ru.tpu.hostel.booking.service.BookingService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {
    @Override
    public BookingResponse createBooking(BookingTimeLineRequest bookingTimeLineRequestDto, UUID userId) {
        return null;
    }

    @Override
    public BookingResponse createBooking(BookingTimeSlotRequest bookingTimeSlotRequestDto, UUID userId) {
        return null;
    }

    @Override
    public List<BookingShortResponse> getAvailableTimeBookings(LocalDate date, BookingType bookingType) {
        return List.of();
    }

    @Override
    public List<TimeSlotResponse> getAvailableTimeBooking(LocalDate date, BookingType bookingType, UUID userId) {
        return List.of();
    }

    @Override
    public BookingResponse cancelBooking(UUID bookingId, UUID userId) {
        return null;
    }

    @Override
    public BookingResponse getBooking(UUID bookingId) {
        return null;
    }

    @Override
    public List<BookingResponse> getBookingsByStatus(BookingStatus status, UUID userId) {
        return List.of();
    }

    @Override
    public List<BookingResponse> getBookingsByUser(UUID userId) {
        return List.of();
    }

    @Override
    public List<BookingResponseWithUser> getBookingsByTypeAndDate(BookingType bookingType, LocalDate date) {
        return List.of();
    }

    @Override
    public List<BookingResponseWithUser> getBookingsByDate(LocalDate date) {
        return List.of();
    }
}
