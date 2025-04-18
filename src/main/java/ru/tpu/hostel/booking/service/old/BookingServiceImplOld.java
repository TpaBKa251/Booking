package ru.tpu.hostel.booking.service.old;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.common.exception.ServiceException;
import ru.tpu.hostel.booking.dto.request.BookingTimeLineRequest;
import ru.tpu.hostel.booking.dto.request.BookingTimeSlotRequest;
import ru.tpu.hostel.booking.dto.response.BookingResponse;
import ru.tpu.hostel.booking.dto.response.BookingResponseWithUser;
import ru.tpu.hostel.booking.dto.response.BookingShortResponse;
import ru.tpu.hostel.booking.dto.response.TimeSlotResponse;
import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.entity.BookingType;
import ru.tpu.hostel.booking.external.rest.user.UserServiceClient;
import ru.tpu.hostel.booking.mapper.BookingMapperOld;
import ru.tpu.hostel.booking.repository.BookingRepositoryOld;
import ru.tpu.hostel.booking.service.impl.BookingServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Этот класс устарел и будет удалён в будущем.
 * Вместо него используйте {@link BookingServiceImpl}.
 * @deprecated Класс заменён на {@link BookingServiceImpl}.
 *
 * @see BookingServiceImpl
 */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImplOld implements BookingServiceOld {

    private final BookingRepositoryOld bookingRepository;
    private final TimeLineBookingWay timeLineBookingWay;
    private final TimeSlotBookingWay timeSlotBookingWay;
    private final UserServiceClient userServiceClient;

    /**
     * @deprecated убрана возможность брони по таймлайну
     */
    @Deprecated
    @Override
    public BookingResponse createBooking(BookingTimeLineRequest bookingTimeLineRequestDto, UUID userId) {
        if (!bookingTimeLineRequestDto.bookingType().equals(BookingType.HALL)) {
            throw new ServiceException.BadRequest("Вы не можете забронировать слотовую на кастомное время");
        }
        //checkUser(userId);

        return timeLineBookingWay.createBooking(bookingTimeLineRequestDto, userId);
    }

    @Override
    public BookingResponse createBooking(BookingTimeSlotRequest bookingTimeSlotRequestDto, UUID userId) {
        //checkUser(userId);

        return timeSlotBookingWay.createBooking(bookingTimeSlotRequestDto, userId);
    }

    /**
     * @deprecated убрана возможность брони по таймлайну
     */
    @Deprecated
    @Override
    public List<BookingShortResponse> getAvailableTimeBookings(LocalDate date, BookingType bookingType) {
        return timeLineBookingWay.getAvailableTimeBookings(date, bookingType);
    }

    @Override
    public List<TimeSlotResponse> getAvailableTimeBooking(LocalDate date, BookingType bookingType, UUID userId) {
        return timeSlotBookingWay.getAvailableTimeSlots(date, bookingType, userId);
    }

    @Override
    public BookingResponse cancelBooking(UUID bookingId, UUID userId) {
        //checkUser(userId);

        BookingOld booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ServiceException.NotFound("Бронь не найдена"));

        if (!booking.getUser().equals(userId)) {
            List<String> userRoles = userServiceClient.getAllRolesByUserId(userId);

            log.info(userRoles.toString());

            for (String userRole : userRoles) {
                if (userRole.contains(booking.getType().toString())
                || userRole.contains("HOSTEL_SUPERVISOR")
                || userRole.contains("ADMINISTRATION")) {
                    log.info("{}, прошли", userRole);
                    booking.getBookingState().cancelBooking(booking, bookingRepository);

                    return BookingMapperOld.mapBookingToBookingResponseDto(booking);
                }
            }

            throw new ServiceException.Forbidden("Вы не можете закрывать чужие брони");
        }

        booking.getBookingState().cancelBooking(booking, bookingRepository);

        return BookingMapperOld.mapBookingToBookingResponseDto(booking);
    }

    @Override
    public BookingResponse getBooking(UUID bookingId) {
        return BookingMapperOld.mapBookingToBookingResponseDto(bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ServiceException.NotFound("Бронь не найдена")));
    }

    @Override
    public List<BookingResponse> getBookingsByStatus(BookingStatus status, UUID userId) {
        //checkUser(userId);

        List<BookingOld> bookings = bookingRepository.findAllByStatusAndUser(status, userId);

        return bookings
                .stream()
                .map(BookingMapperOld::mapBookingToBookingResponseDto)
                .toList();
    }

    @Override
    public List<BookingResponse> getBookingsByUser(UUID userId) {
        //checkUser(userId);

        List<BookingOld> bookings = bookingRepository.findAllByUser(userId);

        return bookings
                .stream()
                .map(BookingMapperOld::mapBookingToBookingResponseDto)
                .toList();
    }

    @Override
    public List<BookingResponseWithUser> getBookingsByTypeAndDate(BookingType bookingType, LocalDate date) {
        return bookingRepository.findAllByTypeAndStartTimeOnSpecificDay(
                        bookingType,
                        date.atTime(0, 0, 0),
                        date.atTime(23, 59, 59)
                )
                .stream()
                .map(BookingMapperOld::mapBookingToBookingResponseWithUserDto)
                .toList();
    }

    @Override
    public List<BookingResponseWithUser> getBookingsByDate(LocalDate date) {
        return bookingRepository.findAllBookingsOnSpecificDay(
                        date.atTime(0, 0, 0),
                        date.atTime(23, 59, 59)
                )
                .stream()
                .map(BookingMapperOld::mapBookingToBookingResponseWithUserDto)
                .toList();
    }

    /**
     * @deprecated функционал перенесен в API Gateway
     */
    @Deprecated
    private void checkUser(UUID userId) {
        userServiceClient.getUserById(userId);
    }
}
