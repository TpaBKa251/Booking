package ru.tpu.hostel.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.dto.request.BookingTimeLineRequestDto;
import ru.tpu.hostel.booking.dto.request.BookingTimeSlotRequestDto;
import ru.tpu.hostel.booking.dto.response.BookingResponseDto;
import ru.tpu.hostel.booking.dto.response.BookingShortResponseDto;
import ru.tpu.hostel.booking.dto.response.TimeSlotResponseDto;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.enums.BookingStatus;
import ru.tpu.hostel.booking.enums.BookingType;
import ru.tpu.hostel.booking.exception.BookingNotFoundException;
import ru.tpu.hostel.booking.mapper.BookingMapper;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.booking.service.BookingService;
import ru.tpu.hostel.booking.service.impl.way.TimeLineBookingWay;
import ru.tpu.hostel.booking.service.impl.way.TimeSlotBookingWay;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TimeLineBookingWay timeLineBookingWay;
    private final TimeSlotBookingWay timeSlotBookingWay;

    @Override
    public BookingResponseDto createBooking(BookingTimeLineRequestDto bookingTimeLineRequestDto, UUID userId) {
        return timeLineBookingWay.createBooking(bookingTimeLineRequestDto, userId);
    }

    @Override
    public BookingResponseDto createBooking(BookingTimeSlotRequestDto bookingTimeSlotRequestDto, UUID userId) {
        return timeSlotBookingWay.createBooking(bookingTimeSlotRequestDto, userId);
    }

    @Override
    public List<BookingShortResponseDto> getAvailableTimeBookings(LocalDate date, BookingType bookingType) {
        return timeLineBookingWay.getAvailableTimeBookings(date, bookingType);
    }

    @Override
    public List<TimeSlotResponseDto> getAvailableTimeBooking(LocalDate date, BookingType bookingType) {
        return timeSlotBookingWay.getAvailableTimeSlots(date, bookingType);
    }

    @Override
    public BookingResponseDto cancelBooking(UUID bookingId, UUID userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Бронь не найдена"));

        if (!booking.getUser().equals(userId)) {
            throw new BookingNotFoundException("Бронь не найдена");
        }

        booking.getBookingState().cancelBooking(booking, bookingRepository);

        return BookingMapper.mapBookingToBookingResponseDto(booking);
    }

    @Override
    public BookingResponseDto getBooking(UUID bookingId) {
        return null;
    }

    @Override
    public List<BookingResponseDto> getBookingsByStatus(BookingStatus status, UUID userId) {
        return List.of();
    }
}
