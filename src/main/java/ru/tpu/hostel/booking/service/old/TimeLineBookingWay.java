package ru.tpu.hostel.booking.service.old;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.common.exception.ServiceException;
import ru.tpu.hostel.booking.common.utils.TimeUtil;
import ru.tpu.hostel.booking.dto.request.BookingTimeLineRequest;
import ru.tpu.hostel.booking.dto.response.BookingResponse;
import ru.tpu.hostel.booking.dto.response.BookingShortResponse;
import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.entity.BookingType;
import ru.tpu.hostel.booking.mapper.BookingMapperOld;
import ru.tpu.hostel.booking.repository.BookingRepositoryOld;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @deprecated Удалена возможность брони по таймлайну
 */
@Deprecated
@Service
@RequiredArgsConstructor
public class TimeLineBookingWay {

    private final BookingRepositoryOld bookingRepository;

    private LocalTime startBookingTime = LocalTime.of(6, 0);
    private LocalTime endBookingTime = LocalTime.of(0, 0);

    public BookingResponse createBooking(BookingTimeLineRequest bookingTimeLineRequestDto, UUID userId) {
        if (
                bookingTimeLineRequestDto.startTime().isBefore(TimeUtil.now())
                        || bookingTimeLineRequestDto.endTime().isBefore(TimeUtil.now())
        ) {
            throw new ServiceException.BadRequest("Неверное время бронирования");
        }

        if (!bookingTimeLineRequestDto.startTime().toLocalDate()
                .equals(bookingTimeLineRequestDto.endTime().toLocalDate())
        ) {
            if (!bookingTimeLineRequestDto.endTime().minusDays(1).toLocalDate()
                    .equals(bookingTimeLineRequestDto.startTime().toLocalDate())
            ) {
                throw new ServiceException.BadRequest("Неверное время бронирования");
            }

            if (!bookingTimeLineRequestDto.endTime().toLocalTime().equals(endBookingTime)) {
                throw new ServiceException.BadRequest("Дни старта и конца брони должны быть одинаковыми");
            }
        }

        if (bookingTimeLineRequestDto.startTime().getHour() < startBookingTime.getHour()) {
            throw new ServiceException.BadRequest("Бронь должна быть между 6:00 и 23:59");
        }

        if (
                bookingTimeLineRequestDto.startTime().isAfter(bookingTimeLineRequestDto.endTime())
                        || bookingTimeLineRequestDto.startTime().equals(bookingTimeLineRequestDto.endTime())
        ) {
            throw new ServiceException.BadRequest("Стартовое время должно быть раньше конечного");
        }

        List<BookingOld> bookedBookings = bookingRepository
                .findAllByStatusAndType(BookingStatus.BOOKED, bookingTimeLineRequestDto.bookingType());

        for (BookingOld booking : bookedBookings) {
            if (bookingTimeLineRequestDto.startTime().isBefore(booking.getEndTime())
                    && bookingTimeLineRequestDto.endTime().isAfter(booking.getStartTime())) {
                throw new ServiceException.BadRequest(
                        "Ваша бронь пересекается с другой: "
                                + BookingMapperOld.mapBookingToBookingShortResponseDto(booking)
                );
            }
        }

        BookingOld booking = new BookingOld();
        booking.setStartTime(bookingTimeLineRequestDto.startTime().withMinute(0).withSecond(0).minusNanos(0));
        booking.setEndTime(bookingTimeLineRequestDto.endTime().withMinute(0).withSecond(0).minusNanos(0));
        booking.setStatus(BookingStatus.BOOKED);
        booking.setType(bookingTimeLineRequestDto.bookingType());
        booking.setUser(userId);

        bookingRepository.save(booking);

        return BookingMapperOld.mapBookingToBookingResponseDto(booking);
    }

    public List<BookingShortResponse> getAvailableTimeBookings(LocalDate date, BookingType bookingType) {
        if (LocalDate.now().plusDays(7).isBefore(date) || date.isBefore(TimeUtil.now().toLocalDate())) {
            throw new ServiceException.BadRequest("Вы можете просматривать и бронировать только на неделю вперед");
        }

        List<BookingOld> bookedBookings = bookingRepository.findAllByStatusAndType(BookingStatus.BOOKED, bookingType);

        if (bookedBookings.isEmpty()) {
            return List.of(
                    new BookingShortResponse(
                            startBookingTime.atDate(date),
                            endBookingTime.atDate(date.plusDays(1))
                    )
            );
        }

        List<BookingShortResponse> availableBookingTime = new ArrayList<>();

        LocalTime currentStartTime;

        if (!date.equals(TimeUtil.now().toLocalDate())) {
            currentStartTime = startBookingTime;
        } else {
            currentStartTime = TimeUtil.now().toLocalTime()
                    .plusHours(1)
                    .withMinute(0)
                    .withSecond(0)
                    .minusNanos(0);
        }

        for (BookingOld booking : bookedBookings) {
            if (booking.getStartTime().toLocalTime().isAfter(currentStartTime)
                    && booking.getStartTime().toLocalDate().equals(date)
            ) {
                availableBookingTime.add(
                        new BookingShortResponse(
                                LocalDateTime.of(booking.getStartTime().toLocalDate(), currentStartTime),
                                booking.getStartTime()
                        )
                );
            }

            if (booking.getStartTime().toLocalDate().equals(date)) {
                currentStartTime = booking.getEndTime().toLocalTime();
            }
        }

        if (currentStartTime.isAfter(endBookingTime) || currentStartTime.equals(startBookingTime)) {
            availableBookingTime.add(new BookingShortResponse(
                    LocalDateTime.of(date, currentStartTime),
                    LocalDateTime.of(date.plusDays(1), endBookingTime)
            ));
        }

        return availableBookingTime;
    }
}
