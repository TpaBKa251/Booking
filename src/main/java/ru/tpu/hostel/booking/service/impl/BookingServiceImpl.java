package ru.tpu.hostel.booking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.booking.dto.request.BookingTimeSlotRequest;
import ru.tpu.hostel.booking.dto.response.BookingResponse;
import ru.tpu.hostel.booking.dto.response.BookingResponseWithUser;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.entity.BookingType;
import ru.tpu.hostel.booking.external.amqp.schedule.ScheduleMessageType;
import ru.tpu.hostel.booking.external.amqp.schedule.dto.Failure;
import ru.tpu.hostel.booking.external.amqp.schedule.dto.ScheduleResponse;
import ru.tpu.hostel.booking.external.amqp.schedule.dto.Timeslot;
import ru.tpu.hostel.booking.mapper.BookingMapper;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.booking.service.BookingService;
import ru.tpu.hostel.booking.service.state.BookingState;
import ru.tpu.hostel.booking.utils.NotificationUtil;
import ru.tpu.hostel.internal.exception.ServiceException;
import ru.tpu.hostel.internal.external.amqp.AmqpMessageSender;
import ru.tpu.hostel.internal.external.amqp.dto.NotificationType;
import ru.tpu.hostel.internal.service.NotificationSender;
import ru.tpu.hostel.internal.utils.ExecutionContext;
import ru.tpu.hostel.internal.utils.Roles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.tpu.hostel.booking.entity.BookingStatus.BOOKED;

/**
 * Реализация сервиса броней {@link BookingService}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final AmqpMessageSender amqpMessageSender;

    private final BookingMapper bookingMapper;

    private final NotificationSender notificationSender;

    private final Map<BookingStatus, BookingState> bookingStates;

    @Transactional
    @Override
    public BookingResponse createBooking(BookingTimeSlotRequest bookingTimeSlotRequest) {
        UUID userId = ExecutionContext.get().getUserID();

        ScheduleResponse scheduleResponse = amqpMessageSender.sendAndReceive(
                ScheduleMessageType.BOOK,
                bookingTimeSlotRequest.slotId().toString(),
                bookingTimeSlotRequest.slotId(),
                ScheduleResponse.class
        );

        Booking booking = getBooking(userId, scheduleResponse);
        try {
            bookingRepository.save(booking);
            bookingRepository.flush();
            notificationSender.sendNotification(
                    userId,
                    NotificationType.BOOKING,
                    NotificationUtil.getNotificationTitleForBook(booking.getType()),
                    NotificationUtil.getNotificationMessageForBook(
                            booking.getType(),
                            booking.getStartTime(),
                            booking.getEndTime()
                    )
            );
            return bookingMapper.mapToBookingResponse(booking);
        } catch (DataIntegrityViolationException e) {
            sendMessageCancellation(bookingTimeSlotRequest.slotId(), bookingTimeSlotRequest.slotId(), false);
            throw new ServiceException.Conflict("Вы не можете забронировать слот повторно");
        }
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

    @Transactional
    @Override
    public BookingResponse cancelBooking(UUID bookingId) {
        ExecutionContext context = ExecutionContext.get();
        Booking bookingToCancel = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new ServiceException.NotFound("Бронь не найдена"));

        if (bookingToCancel.getUser().equals(context.getUserID())) {
            processCancellation(bookingToCancel, true);
        } else if (Roles.hasPermissionToManageResourceType(context.getUserRoles(), bookingToCancel.getType())) {
            processCancellation(bookingToCancel, false);
        } else {
            throw new ServiceException.Forbidden("Вы не можете закрыть чужую бронь");
        }

        return bookingMapper.mapToBookingResponse(bookingToCancel);
    }

    @Transactional
    @Override
    public BookingResponse cancelBookingByTimeslot(UUID timeslotId) {
        ExecutionContext context = ExecutionContext.get();
        Booking bookingToCancel = bookingRepository.findByUserAndTimeSlotForUpdate(context.getUserID(), timeslotId)
                .orElseThrow(() -> new ServiceException.NotFound("Бронь не найдена"));

        processCancellation(bookingToCancel, true);
        return bookingMapper.mapToBookingResponse(bookingToCancel);
    }

    private void processCancellation(Booking bookingToCancel, boolean cancelledByOwner) {
        bookingStates.get(bookingToCancel.getStatus()).cancelBooking(bookingToCancel);
        sendMessageCancellation(bookingToCancel.getId(), bookingToCancel.getTimeSlot(), true);
        notificationSender.sendNotification(
                ExecutionContext.get().getUserID(),
                NotificationType.BOOKING,
                NotificationUtil.getNotificationTitleForCancel(bookingToCancel.getType(), cancelledByOwner),
                NotificationUtil.getNotificationMessageForCancel(
                        bookingToCancel.getType(),
                        bookingToCancel.getStartTime(),
                        bookingToCancel.getEndTime(),
                        cancelledByOwner
                )
        );
    }

    private void sendMessageCancellation(UUID bookingId, UUID timeSlotId, boolean transacted) {
        ScheduleMessageType type = transacted
                ? ScheduleMessageType.CANCEL
                : ScheduleMessageType.CANCEL_WITHOUT_TRANSACTION;
        try {
            amqpMessageSender.send(type, bookingId.toString(), timeSlotId);
        } catch (ServiceException e) {
            throw new ServiceException("Не можем закрыть бронь, попробуйте позже", e.getStatus());
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingResponse> getUserBookingsByStatus(BookingStatus status, UUID userId) {
        return bookingRepository.findAllByStatusAndUser(status, userId)
                .stream()
                .map(bookingMapper::mapToBookingResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<UUID> getUserBookingsByStatusShort(UUID userId, LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        return bookingRepository.findAllBookedTimeslotIdsByUser(userId, dayStart, dayStart.plusDays(1));
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
        LocalDateTime dayStart = date.atStartOfDay();
        return bookingRepository.findAllBookedBookingsByTypeAndStartTimeOnSpecificDay(
                        bookingType,
                        dayStart,
                        dayStart.plusDays(1)
                )
                .stream()
                .map(bookingMapper::mapToBookingResponseWithUser)
                .toList();
    }

    @Override
    public List<BookingResponseWithUser> getBookingsByDateWithUser(LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        return bookingRepository.findAllBookedBookingsOnSpecificDay(dayStart, dayStart.plusDays(1))
                .stream()
                .map(bookingMapper::mapToBookingResponseWithUser)
                .toList();
    }

}
