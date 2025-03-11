package ru.tpu.hostel.booking.service.impl.way;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.dto.request.BookingTimeSlotRequest;
import ru.tpu.hostel.booking.dto.response.BookingResponse;
import ru.tpu.hostel.booking.dto.response.TimeSlotResponse;
import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.entity.TimeSlot;
import ru.tpu.hostel.booking.enums.BookingStatus;
import ru.tpu.hostel.booking.enums.BookingType;
import ru.tpu.hostel.booking.exception.InvalidTimeBookingException;
import ru.tpu.hostel.booking.exception.SlotAlreadyBookedException;
import ru.tpu.hostel.booking.exception.SlotNotFoundException;
import ru.tpu.hostel.booking.mapper.BookingMapperOld;
import ru.tpu.hostel.booking.mapper.SlotMapper;
import ru.tpu.hostel.booking.rabbit.amqp.AmqpMessageSender;
import ru.tpu.hostel.booking.repository.BookingRepositoryOld;
import ru.tpu.hostel.booking.repository.ResponsibleRepository;
import ru.tpu.hostel.booking.repository.TimeSlotRepository;
import ru.tpu.hostel.booking.service.impl.BookingServiceImplOld;
import ru.tpu.hostel.booking.utils.TimeNow;

import java.io.IOException;
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
        TimeSlot timeSlot = timeSlotRepository.findById(bookingTimeSlotRequestDto.slotId())
                .orElseThrow(() -> new SlotNotFoundException("Слот не найден"));

        try {
            schedulesServiceAmqpMessageSender.sendAndReceive(
                    bookingTimeSlotRequestDto.slotId().toString(),
                    bookingTimeSlotRequestDto.slotId()
            );
        } catch (IOException e) {
            log.error("Ответ так и не получили", e);
        }

        List<BookingOld> bookings = bookingRepository.findAllByStatusNotAndTimeSlot(BookingStatus.CANCELLED, timeSlot);

        if (bookings.size() == timeSlot.getLimit()) {
            throw new SlotAlreadyBookedException("Слот уже забронирован");
        }

        if (timeSlot.getStartTime().isBefore(TimeNow.now())) {
            throw new InvalidTimeBookingException("Вы можете забронировать только слоты,"
                    + " время начала которых позже текущего времени");
        }

        // Возможно это не надо
        if (bookingRepository.findByTimeSlotAndUserAndStatus(timeSlot, userId, BookingStatus.BOOKED).isPresent()) {
            throw new InvalidTimeBookingException("Вы не можете забронировать слот повторно");
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
        if (LocalDate.now().plusDays(7).isBefore(date) || date.isBefore(TimeNow.now().toLocalDate())) {
            throw new InvalidTimeBookingException("Вы можете просматривать и бронировать слоты только на неделю вперед");
        }

        List<TimeSlot> timeSlots = timeSlotRepository.findByType(bookingType);

        List<TimeSlotResponse> availableSlots = new ArrayList<>();

        for (TimeSlot timeSlot : timeSlots) {
            if (timeSlot.getStartTime().toLocalDate().equals(date)
                    && timeSlot.getStartTime().isAfter(TimeNow.now())
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
