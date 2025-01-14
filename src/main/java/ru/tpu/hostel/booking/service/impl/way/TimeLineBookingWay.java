package ru.tpu.hostel.booking.service.impl.way;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.dto.request.BookingTimeLineRequestDto;
import ru.tpu.hostel.booking.dto.response.BookingResponseDto;
import ru.tpu.hostel.booking.dto.response.BookingShortResponseDto;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.enums.BookingStatus;
import ru.tpu.hostel.booking.enums.BookingType;
import ru.tpu.hostel.booking.exception.InvalidTimeBookingException;
import ru.tpu.hostel.booking.mapper.BookingMapper;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.booking.utils.TimeNow;

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

    private final BookingRepository bookingRepository;

    private LocalTime startBookingTime = LocalTime.of(6, 0);
    private LocalTime endBookingTime = LocalTime.of(0, 0);

    public BookingResponseDto createBooking(BookingTimeLineRequestDto bookingTimeLineRequestDto, UUID userId) {
        if (
                bookingTimeLineRequestDto.startTime().isBefore(TimeNow.now())
                        || bookingTimeLineRequestDto.endTime().isBefore(TimeNow.now())
        ) {
            throw new InvalidTimeBookingException("Неверное время бронирования");
        }

        if (!bookingTimeLineRequestDto.startTime().toLocalDate()
                .equals(bookingTimeLineRequestDto.endTime().toLocalDate())
        ) {
            if (!bookingTimeLineRequestDto.endTime().minusDays(1).toLocalDate()
                    .equals(bookingTimeLineRequestDto.startTime().toLocalDate())
            ) {
                throw new InvalidTimeBookingException("Неверное время бронирования");
            }

            if (!bookingTimeLineRequestDto.endTime().toLocalTime().equals(endBookingTime)) {
                throw new InvalidTimeBookingException("Дни старта и конца брони должны быть одинаковыми");
            }
        }

        if (bookingTimeLineRequestDto.startTime().getHour() < startBookingTime.getHour()) {
            throw new InvalidTimeBookingException("Бронь должна быть между 6:00 и 23:59");
        }

        if (
                bookingTimeLineRequestDto.startTime().isAfter(bookingTimeLineRequestDto.endTime())
                        || bookingTimeLineRequestDto.startTime().equals(bookingTimeLineRequestDto.endTime())
        ) {
            throw new InvalidTimeBookingException("Стартовое время должно быть раньше конечного");
        }

        List<Booking> bookedBookings = bookingRepository
                .findAllByStatusAndType(BookingStatus.BOOKED, bookingTimeLineRequestDto.bookingType());

        for (Booking booking : bookedBookings) {
            if (bookingTimeLineRequestDto.startTime().isBefore(booking.getEndTime())
                    && bookingTimeLineRequestDto.endTime().isAfter(booking.getStartTime())) {
                throw new InvalidTimeBookingException(
                        "Ваша бронь пересекается с другой: "
                                + BookingMapper.mapBookingToBookingShortResponseDto(booking)
                );
            }
        }

        Booking booking = new Booking();
        booking.setStartTime(bookingTimeLineRequestDto.startTime().withMinute(0).withSecond(0).minusNanos(0));
        booking.setEndTime(bookingTimeLineRequestDto.endTime().withMinute(0).withSecond(0).minusNanos(0));
        booking.setStatus(BookingStatus.BOOKED);
        booking.setType(bookingTimeLineRequestDto.bookingType());
        booking.setUser(userId);

        bookingRepository.save(booking);

        return BookingMapper.mapBookingToBookingResponseDto(booking);
    }

    public List<BookingShortResponseDto> getAvailableTimeBookings(LocalDate date, BookingType bookingType) {
        if (LocalDate.now().plusDays(7).isBefore(date) || date.isBefore(TimeNow.now().toLocalDate())) {
            throw new InvalidTimeBookingException("Вы можете просматривать и бронировать только на неделю вперед");
        }

        List<Booking> bookedBookings = bookingRepository.findAllByStatusAndType(BookingStatus.BOOKED, bookingType);

        if (bookedBookings.isEmpty()) {
            return List.of(
                    new BookingShortResponseDto(
                            startBookingTime.atDate(date),
                            endBookingTime.atDate(date.plusDays(1))
                    )
            );
        }

        List<BookingShortResponseDto> availableBookingTime = new ArrayList<>();

        LocalTime currentStartTime;

        if (!date.equals(TimeNow.now().toLocalDate())) {
            currentStartTime = startBookingTime;
        } else {
            currentStartTime = TimeNow.now().toLocalTime()
                    .plusHours(1)
                    .withMinute(0)
                    .withSecond(0)
                    .minusNanos(0);
        }

        for (Booking booking : bookedBookings) {
            if (booking.getStartTime().toLocalTime().isAfter(currentStartTime)
                    && booking.getStartTime().toLocalDate().equals(date)
            ) {
                availableBookingTime.add(
                        new BookingShortResponseDto(
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
            availableBookingTime.add(new BookingShortResponseDto(
                    LocalDateTime.of(date, currentStartTime),
                    LocalDateTime.of(date.plusDays(1), endBookingTime)
            ));
        }

        return availableBookingTime;
    }
}
