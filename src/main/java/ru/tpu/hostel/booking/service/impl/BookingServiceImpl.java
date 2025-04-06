package ru.tpu.hostel.booking.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.booking.common.exception.ServiceException;
import ru.tpu.hostel.booking.common.utils.TimeUtil;
import ru.tpu.hostel.booking.dto.request.BookingTimeSlotRequest;
import ru.tpu.hostel.booking.dto.response.BookingResponse;
import ru.tpu.hostel.booking.dto.response.BookingResponseWithUser;
import ru.tpu.hostel.booking.dto.response.TimeSlotResponse;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.entity.BookingType;
import ru.tpu.hostel.booking.external.amqp.AmqpMessageSender;
import ru.tpu.hostel.booking.external.amqp.schedule.dto.Timeslot;
import ru.tpu.hostel.booking.external.rest.user.UserServiceClient;
import ru.tpu.hostel.booking.mapper.BookingMapper;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.booking.service.BookingService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.tpu.hostel.booking.entity.BookingStatus.BOOKED;
import static ru.tpu.hostel.booking.entity.BookingStatus.CANCELLED;

/**
 * Реализация сервиса броней {@link BookingService}
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final AmqpMessageSender schedulesServiceAmqpMessageSender;

    private final UserServiceClient userServiceClient;

    private final BookingMapper bookingMapper;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Transactional
    @Override
    public BookingResponse createBooking(BookingTimeSlotRequest bookingTimeSlotRequest, UUID userId) {
        Timeslot timeslot;
        try {
            Message message = schedulesServiceAmqpMessageSender.sendAndReceive(
                    bookingTimeSlotRequest.slotId().toString(),
                    bookingTimeSlotRequest.slotId()
            );
            timeslot = objectMapper.readValue(message.getBody(), Timeslot.class);
        } catch (Exception e) {
            throw new ServiceException.ServiceUnavailable("Сервис расписаний не отвечает", e);
        }

        List<Booking> bookings = bookingRepository.findAllByStatusNotAndTimeSlot(CANCELLED, timeslot.id());
        if (bookings.size() == timeslot.limit()) {
            throw new ServiceException.BadRequest("Слот уже забронирован");
        }
        if (timeslot.startTime().isBefore(TimeUtil.now())) {
            throw new ServiceException.BadRequest("Вы можете забронировать только слоты,"
                    + " время начала которых позже текущего времени");
        }

        // Возможно это не надо
        Optional<Booking> bookingOptional = bookingRepository.findByTimeSlotAndUserAndStatus(
                timeslot.id(),
                userId,
                BOOKED
        );
        if (bookingOptional.isPresent()) {
            throw new ServiceException.BadRequest("Вы не можете забронировать слот повторно");
        }

        Booking booking = new Booking();
        booking.setUser(userId);
        booking.setTimeSlot(timeslot.id());
        booking.setStartTime(timeslot.startTime());
        booking.setEndTime(timeslot.endTime());
        booking.setStatus(BOOKED);
        booking.setType(timeslot.type());
        bookingRepository.save(booking);

        return bookingMapper.mapToBookingResponse(booking);
    }

    @Override
    public List<TimeSlotResponse> getAvailableTimeslotsForBooking(
            LocalDate date,
            BookingType bookingType,
            UUID userId
    ) {
        // TODO: после задачи HOSTEL-16 https://clck.ru/3J5LfV либо удалить этот метод, либо реализовать
        return List.of();
    }

    @Override
    public BookingResponse cancelBooking(UUID bookingId, UUID userId) {
        Booking bookingToCancel = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ServiceException.NotFound("Бронь не найдена"));

        if (bookingToCancel.getUser().equals(userId)) {
            bookingToCancel.getBookingState().cancelBooking(bookingToCancel, bookingRepository);
            return bookingMapper.mapToBookingResponse(bookingToCancel);
        }

        // TODO: поменять FeignClinet у юзера на RabbitMQ по RPC
        List<String> userRoles = userServiceClient.getAllRolesByUserId(userId);
        for (String userRole : userRoles) {
            if (userRole.contains(bookingToCancel.getType().toString())
                    || userRole.contains("HOSTEL_SUPERVISOR")
                    || userRole.contains("ADMINISTRATION")) {
                bookingToCancel.getBookingState().cancelBooking(bookingToCancel, bookingRepository);
                return bookingMapper.mapToBookingResponse(bookingToCancel);
            }
        }

        throw new ServiceException.Forbidden("Вы не можете закрыть чужую бронь");
    }

    @Override
    public List<BookingResponse> getUserBookingsByStatus(BookingStatus status, UUID userId) {
        return bookingRepository.findAllByStatusAndUser(status, userId)
                .stream()
                .map(bookingMapper::mapToBookingResponse)
                .toList();
    }

    @Override
    public List<BookingResponse> getBookingsByUser(UUID userId) {
        return bookingRepository.findAllByUser(userId)
                .stream()
                .map(bookingMapper::mapToBookingResponse)
                .toList();
    }

    @Override
    public List<BookingResponseWithUser> getBookingsByTypeAndDateWithUser(BookingType bookingType, LocalDate date) {
        return bookingRepository.findAllBookedBookingsByTypeAndStartTimeOnSpecificDay(bookingType, date)
                .stream()
                .map(bookingMapper::mapToBookingResponseWithUser)
                .toList();
    }

    @Override
    public List<BookingResponseWithUser> getBookingsByDateWithUser(LocalDate date) {
        return bookingRepository.findAllBookedBookingsOnSpecificDay(date)
                .stream()
                .map(bookingMapper::mapToBookingResponseWithUser)
                .toList();
    }

}
