package ru.tpu.hostel.booking.service.impl.way;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.dto.request.BookingTimeSlotRequestDto;
import ru.tpu.hostel.booking.dto.response.BookingResponseDto;
import ru.tpu.hostel.booking.dto.response.TimeSlotResponseDto;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.TimeSlot;
import ru.tpu.hostel.booking.enums.BookingStatus;
import ru.tpu.hostel.booking.enums.BookingType;
import ru.tpu.hostel.booking.exception.InvalidTimeBookingException;
import ru.tpu.hostel.booking.exception.SlotAlreadyBookedException;
import ru.tpu.hostel.booking.exception.SlotNotFoundException;
import ru.tpu.hostel.booking.mapper.BookingMapper;
import ru.tpu.hostel.booking.mapper.SlotMapper;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.booking.repository.TimeSlotRepository;
import ru.tpu.hostel.booking.utils.TimeNow;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TimeSlotBookingWay {

    private final TimeSlotRepository timeSlotRepository;
    private final BookingRepository bookingRepository;

    public BookingResponseDto createBooking(BookingTimeSlotRequestDto bookingTimeSlotRequestDto, UUID userId) {
        TimeSlot timeSlot = timeSlotRepository.findById(bookingTimeSlotRequestDto.slotId())
                .orElseThrow(() -> new SlotNotFoundException("Слот не найден"));

        List<Booking> bookings = bookingRepository.findAllByTimeSlot(timeSlot)
                .stream()
                .filter(booking -> booking.getStatus() != BookingStatus.CANCELLED)
                .toList();

        if (bookings.size() == timeSlot.getLimit()) {
            throw new SlotAlreadyBookedException("Слот уже забронирован");
        }

        if (timeSlot.getStartTime().isBefore(TimeNow.now())) {
            throw new InvalidTimeBookingException("Вы можете забронировать только слоты,"
                    + " время начала которых позже текущего времени");
        }

        Booking booking = new Booking();
        booking.setUser(userId);
        booking.setTimeSlot(timeSlot);
        booking.setStartTime(timeSlot.getStartTime());
        booking.setEndTime(timeSlot.getEndTime());
        booking.setStatus(BookingStatus.BOOKED);
        booking.setType(timeSlot.getType());
        bookingRepository.save(booking);

        return BookingMapper.mapBookingToBookingResponseDto(booking);
    }

    public List<TimeSlotResponseDto> getAvailableTimeSlots(LocalDate date, BookingType bookingType) {
        if (LocalDate.now().plusDays(7).isBefore(date) || date.isBefore(TimeNow.now().toLocalDate())) {
            throw new InvalidTimeBookingException("Вы можете просматривать и бронировать слоты только на неделю вперед");
        }

        List<TimeSlot> timeSlots = timeSlotRepository.findByType(bookingType);

        List<TimeSlotResponseDto> availableSlots = new ArrayList<>();

        for (TimeSlot timeSlot : timeSlots) {
            if (timeSlot.getStartTime().toLocalDate().equals(date)
                    && timeSlot.getStartTime().isAfter(TimeNow.now())
            ) {
                List<Booking> bookings = bookingRepository.findAllByTimeSlot(timeSlot)
                        .stream()
                        .filter(booking -> booking.getStatus() != BookingStatus.CANCELLED)
                        .toList();

                if (bookings.size() < timeSlot.getLimit()) {
                    availableSlots.add(SlotMapper.mapTimeSlotToTimeSlotResponseDto(timeSlot));
                }
            }
        }

        return availableSlots;
    }
}
