package ru.tpu.hostel.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.client.UserServiceClient;
import ru.tpu.hostel.booking.dto.request.BookingTimeLineRequestDto;
import ru.tpu.hostel.booking.dto.request.BookingTimeSlotRequestDto;
import ru.tpu.hostel.booking.dto.response.BookingResponseDto;
import ru.tpu.hostel.booking.dto.response.BookingShortResponseDto;
import ru.tpu.hostel.booking.dto.response.TimeSlotResponseDto;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.enums.BookingStatus;
import ru.tpu.hostel.booking.enums.BookingType;
import ru.tpu.hostel.booking.exception.BookingNotFoundException;
import ru.tpu.hostel.booking.exception.InvalidTimeBookingException;
import ru.tpu.hostel.booking.exception.UserNotFound;
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
    private final UserServiceClient userServiceClient;

    /**
     * @deprecated убрана возможность брони по таймлайну
     */
    @Deprecated
    @Override
    public BookingResponseDto createBooking(BookingTimeLineRequestDto bookingTimeLineRequestDto, UUID userId) {
        if (!bookingTimeLineRequestDto.bookingType().equals(BookingType.HALL)) {
            throw new InvalidTimeBookingException("Вы не можете забронировать слотовую на кастомное время");
        }
        //checkUser(userId);

        return timeLineBookingWay.createBooking(bookingTimeLineRequestDto, userId);
    }

    @Override
    public BookingResponseDto createBooking(BookingTimeSlotRequestDto bookingTimeSlotRequestDto, UUID userId) {
        //checkUser(userId);

        return timeSlotBookingWay.createBooking(bookingTimeSlotRequestDto, userId);
    }

    /**
     * @deprecated убрана возможность брони по таймлайну
     */
    @Deprecated
    @Override
    public List<BookingShortResponseDto> getAvailableTimeBookings(LocalDate date, BookingType bookingType) {
        return timeLineBookingWay.getAvailableTimeBookings(date, bookingType);
    }

    @Override
    public List<TimeSlotResponseDto> getAvailableTimeBooking(LocalDate date, BookingType bookingType, UUID userId) {
        return timeSlotBookingWay.getAvailableTimeSlots(date, bookingType, userId);
    }

    @Override
    public BookingResponseDto cancelBooking(UUID bookingId, UUID userId) {
        //checkUser(userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(BookingNotFoundException::new);

        if (!booking.getUser().equals(userId)) {
            throw new BookingNotFoundException("Вы не можете закрывать чужие брони");
        }

        booking.getBookingState().cancelBooking(booking, bookingRepository);

        return BookingMapper.mapBookingToBookingResponseDto(booking);
    }

    @Override
    public BookingResponseDto getBooking(UUID bookingId) {
        return BookingMapper.mapBookingToBookingResponseDto(bookingRepository.findById(bookingId)
                        .orElseThrow(BookingNotFoundException::new));
    }

    @Override
    public List<BookingResponseDto> getBookingsByStatus(BookingStatus status, UUID userId) {
        //checkUser(userId);

        List<Booking> bookings = bookingRepository.findAllByStatusAndUser(status, userId);

        return bookings.stream().map(BookingMapper::mapBookingToBookingResponseDto).toList();
    }

    @Override
    public List<BookingResponseDto> getBookingsByUser(UUID userId) {
        //checkUser(userId);

        List<Booking> bookings = bookingRepository.findAllByUser(userId);

        return bookings.stream().map(BookingMapper::mapBookingToBookingResponseDto).toList();
    }

    /**
     * @deprecated функционал перенесен в API Gateway
     */
    @Deprecated
    private void checkUser(UUID userId) {
        ResponseEntity<?> response;

        try {
            response = userServiceClient.getUserById(userId);
        } catch (Exception e) {
            throw new UserNotFound("Пользователь не найден");
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new UserNotFound("Пользователь не найден");
        }
    }
}
