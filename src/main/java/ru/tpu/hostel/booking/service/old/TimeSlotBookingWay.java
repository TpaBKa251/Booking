package ru.tpu.hostel.booking.service.old;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.common.exception.ServiceException;
import ru.tpu.hostel.booking.common.utils.TimeUtil;
import ru.tpu.hostel.booking.dto.request.BookingTimeSlotRequest;
import ru.tpu.hostel.booking.dto.response.BookingResponse;
import ru.tpu.hostel.booking.dto.response.TimeSlotResponse;
import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.entity.BookingType;
import ru.tpu.hostel.booking.entity.TimeSlot;
import ru.tpu.hostel.booking.external.amqp.AmqpMessageSender;
import ru.tpu.hostel.booking.mapper.BookingMapperOld;
import ru.tpu.hostel.booking.mapper.SlotMapper;
import ru.tpu.hostel.booking.repository.BookingRepositoryOld;
import ru.tpu.hostel.booking.repository.ResponsibleRepository;
import ru.tpu.hostel.booking.repository.TimeSlotRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
/**
 * Этот класс устарел и будет удалён в будущем.
 * Вся логика этого класса будет перенесена в класс {@link BookingServiceImplOld}.
 * @deprecated Класс заменён на {@link BookingServiceImplOld}.
 *
 * @see BookingServiceImplOld
 */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
@Service
@RequiredArgsConstructor
public class TimeSlotBookingWay {

    private final TimeSlotRepository timeSlotRepository;

    private final BookingRepositoryOld bookingRepository;

    private final ResponsibleRepository responsibleRepository;

    private final AmqpMessageSender schedulesServiceAmqpMessageSender;

    public BookingResponse createBooking(BookingTimeSlotRequest bookingTimeSlotRequestDto, UUID userId) {
        try {
            schedulesServiceAmqpMessageSender.sendAndReceive(
                    bookingTimeSlotRequestDto.slotId().toString(),
                    bookingTimeSlotRequestDto.slotId()
            );
        } catch (Exception e) {
            log.error("Что-то пошло не так", e);
        }

        TimeSlot timeSlot = timeSlotRepository.findById(bookingTimeSlotRequestDto.slotId())
                .orElseThrow(() -> new ServiceException.NotFound("Слот не найден"));

        List<BookingOld> bookings = bookingRepository.findAllByStatusNotAndTimeSlot(BookingStatus.CANCELLED, timeSlot);

        if (bookings.size() == timeSlot.getLimit()) {
            throw new ServiceException.BadRequest("Слот уже забронирован");
        }

        if (timeSlot.getStartTime().isBefore(TimeUtil.now())) {
            throw new ServiceException.BadRequest("Вы можете забронировать только слоты,"
                    + " время начала которых позже текущего времени");
        }

        // Возможно это не надо
        if (bookingRepository.findByTimeSlotAndUserAndStatus(timeSlot, userId, BookingStatus.BOOKED).isPresent()) {
            throw new ServiceException.BadRequest("Вы не можете забронировать слот повторно");
        }

        BookingOld booking = new BookingOld();
        booking.setUser(userId);
        booking.setTimeSlot(timeSlot);
        booking.setStartTime(timeSlot.getStartTime());
        booking.setEndTime(timeSlot.getEndTime());
        booking.setStatus(BookingStatus.BOOKED);
        booking.setType(timeSlot.getType());
        bookingRepository.save(booking);

        return BookingMapperOld.mapBookingToBookingResponseDto(booking);
    }

    public List<TimeSlotResponse> getAvailableTimeSlots(LocalDate date, BookingType bookingType, UUID userId) {
        if (LocalDate.now().plusDays(7).isBefore(date) || date.isBefore(TimeUtil.now().toLocalDate())) {
            throw new ServiceException.BadRequest("Вы можете просматривать и бронировать слоты только на неделю вперед");
        }

        List<TimeSlot> timeSlots = timeSlotRepository.findByType(bookingType);

        List<TimeSlotResponse> availableSlots = new ArrayList<>();

        for (TimeSlot timeSlot : timeSlots) {
            if (timeSlot.getStartTime().toLocalDate().equals(date)
                    && timeSlot.getStartTime().isAfter(TimeUtil.now())
            ) {
                List<BookingOld> bookings = bookingRepository.findAllByTimeSlot(timeSlot)
                        .stream()
                        .filter(booking -> booking.getStatus() != BookingStatus.CANCELLED)
                        .toList();

                if (bookings.stream().anyMatch(b -> b.getUser().equals(userId))) {
                    continue;
                }

                if (bookings.size() < timeSlot.getLimit()) {
                    availableSlots.add(SlotMapper.mapTimeSlotToTimeSlotResponseDto(timeSlot));
                }
            }
        }

        return availableSlots;
    }
}
