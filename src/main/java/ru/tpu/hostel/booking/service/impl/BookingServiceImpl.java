package ru.tpu.hostel.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.booking.common.exception.ServiceException;
import ru.tpu.hostel.booking.dto.request.BookingTimeSlotRequest;
import ru.tpu.hostel.booking.dto.response.BookingResponse;
import ru.tpu.hostel.booking.dto.response.BookingResponseWithUser;
import ru.tpu.hostel.booking.dto.response.TimeSlotResponse;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.entity.BookingType;
import ru.tpu.hostel.booking.external.amqp.MessageSender;
import ru.tpu.hostel.booking.external.amqp.schedule.ScheduleMessageType;
import ru.tpu.hostel.booking.external.amqp.schedule.dto.Failure;
import ru.tpu.hostel.booking.external.amqp.schedule.dto.ScheduleResponse;
import ru.tpu.hostel.booking.external.amqp.schedule.dto.Timeslot;
import ru.tpu.hostel.booking.mapper.BookingMapper;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.booking.service.BookingService;
import ru.tpu.hostel.booking.service.access.Roles;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static ru.tpu.hostel.booking.entity.BookingStatus.BOOKED;

/**
 * Реализация сервиса броней {@link BookingService}
 */
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final MessageSender<ScheduleMessageType, ScheduleResponse> schedulesServiceMessageSender;

    private final BookingMapper bookingMapper;

    @Transactional
    @Override
    public BookingResponse createBooking(BookingTimeSlotRequest bookingTimeSlotRequest, UUID userId) {
        bookingRepository.findByTimeSlotAndUserAndStatus(bookingTimeSlotRequest.slotId(), userId, BOOKED)
                .ifPresent(booking -> {
                    throw new ServiceException.BadRequest("Вы не можете забронировать слот повторно");
                });

        ScheduleResponse scheduleResponse;
        try {
            scheduleResponse = schedulesServiceMessageSender.sendAndReceive(
                    ScheduleMessageType.BOOK,
                    bookingTimeSlotRequest.slotId().toString(),
                    bookingTimeSlotRequest.slotId()
            );
        } catch (IOException e) {
            throw new ServiceException.ServiceUnavailable("Сервис расписаний не отвечает, попробуйте позже", e);
        }

        Booking booking = getBooking(userId, scheduleResponse);
        bookingRepository.save(booking);

        return bookingMapper.mapToBookingResponse(booking);
    }

    private Booking getBooking(UUID userId, ScheduleResponse scheduleResponse) {
        if (scheduleResponse instanceof Failure failure) {
            throw new ServiceException(failure.getMessage(), failure.getHttpStatus());
        }

        Timeslot timeslot = (Timeslot) scheduleResponse;
        Booking booking = new Booking();
        booking.setUser(userId);
        booking.setTimeSlot(timeslot.getId());
        booking.setStartTime(timeslot.getStartTime());
        booking.setEndTime(timeslot.getEndTime());
        booking.setStatus(BOOKED);
        booking.setType(timeslot.getType());
        return booking;
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

    @Transactional
    @Override
    public BookingResponse cancelBooking(UUID bookingId, UUID userId, Roles[] userRoles) {
        Booking bookingToCancel = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ServiceException.NotFound("Бронь не найдена"));

        if (bookingToCancel.getUser().equals(userId)
                || Roles.hasPermissionToManageResourceType(userRoles, bookingToCancel.getType())) {
            processCancellation(bookingToCancel, userId);
            return bookingMapper.mapToBookingResponse(bookingToCancel);
        }

        throw new ServiceException.Forbidden("Вы не можете закрыть чужую бронь");
    }

    private void processCancellation(Booking bookingToCancel, UUID bookingId) {
        bookingToCancel.getBookingState().cancelBooking(bookingToCancel, bookingRepository);
        try {
            schedulesServiceMessageSender.send(
                    ScheduleMessageType.CANCEL,
                    bookingId.toString(),
                    bookingToCancel.getTimeSlot()
            );
        } catch (IOException e) {
            throw new ServiceException.InternalServerError("Не можем закрыть бронь, попробуйте позже", e);
        }
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
