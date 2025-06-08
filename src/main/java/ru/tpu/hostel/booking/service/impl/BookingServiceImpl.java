package ru.tpu.hostel.booking.service.impl;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.booking.cache.RedissonCacheManager;
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
import ru.tpu.hostel.internal.external.amqp.dto.NotificationRequestDto;
import ru.tpu.hostel.internal.external.amqp.dto.NotificationType;
import ru.tpu.hostel.internal.service.NotificationSender;
import ru.tpu.hostel.internal.utils.ExecutionContext;
import ru.tpu.hostel.internal.utils.Roles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static ru.tpu.hostel.booking.entity.BookingStatus.BOOKED;
import static ru.tpu.hostel.booking.entity.BookingStatus.CANCELLED;
import static ru.tpu.hostel.booking.entity.BookingStatus.IN_PROGRESS;

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

    private final EntityManager entityManager;

    private final RedissonCacheManager<UUID, Timeslot> cacheManager;

    @Transactional
    @Override
    public BookingResponse createBooking(BookingTimeSlotRequest bookingTimeSlotRequest) {
        UUID userId = ExecutionContext.get().getUserID();

        boolean needToUpdateCache = true;
        RLock lock = cacheManager.getLock(bookingTimeSlotRequest.slotId());
        try {
            lock.lock();
            ScheduleResponse scheduleResponse = cacheManager.getCache(bookingTimeSlotRequest.slotId());
            if (scheduleResponse == null) {
                scheduleResponse = amqpMessageSender.sendAndReceive(
                        ScheduleMessageType.BOOK,
                        bookingTimeSlotRequest.slotId().toString(),
                        bookingTimeSlotRequest.slotId(),
                        ScheduleResponse.class
                );
                needToUpdateCache = false;
            } else {
                amqpMessageSender.send(
                        ScheduleMessageType.BOOK,
                        bookingTimeSlotRequest.slotId().toString(),
                        bookingTimeSlotRequest.slotId()
                );
            }

            Booking booking = getBooking(userId, scheduleResponse, needToUpdateCache);
            bookingRepository.save(booking);
            try {
                bookingRepository.flush();
            } catch (DataIntegrityViolationException e) {
                sendMessageCancellation(bookingTimeSlotRequest.slotId(), bookingTimeSlotRequest.slotId(), false);
                throw new ServiceException.Conflict("Вы не можете забронировать слот повторно");
            }

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
        } finally {
            lock.unlock();
        }
    }

    private Booking getBooking(UUID userId, ScheduleResponse scheduleResponse, boolean needToUpdateCache) {
        if (scheduleResponse instanceof Failure failure) {
            throw new ServiceException(failure.getMessage(), failure.getHttpStatus());
        }

        Timeslot timeslot = (Timeslot) scheduleResponse;
        if (!timeslot.isAvailable()) {
            throw new ServiceException.Conflict("Слот занят");
        }
        Booking booking = new Booking();
        booking.setUser(userId);
        booking.setTimeSlot(timeslot.getId());
        booking.setStartTime(timeslot.getStartTime());
        booking.setEndTime(timeslot.getEndTime());
        booking.setStatus(BOOKED);
        booking.setType(timeslot.getType());

        if (needToUpdateCache) {
            timeslot.setBookingCount(timeslot.getBookingCount() + 1);
        }
        cacheManager.putCache(timeslot.getId(), timeslot);
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
        UUID userId = ExecutionContext.get().getUserID();
        Booking bookingToCancel = bookingRepository.findByUserAndTimeSlotForUpdate(userId, timeslotId)
                .orElseThrow(() -> new ServiceException.NotFound("Бронь не найдена"));

        processCancellation(bookingToCancel, true);
        return bookingMapper.mapToBookingResponse(bookingToCancel);
    }

    private void processCancellation(Booking bookingToCancel, boolean cancelledByOwner) {
        UUID userId = ExecutionContext.get().getUserID();
        bookingStates.get(bookingToCancel.getStatus()).cancelBooking(bookingToCancel);
        sendMessageCancellation(bookingToCancel.getId(), bookingToCancel.getTimeSlot(), true);
        notificationSender.sendNotification(
                userId,
                NotificationType.BOOKING,
                NotificationUtil.getNotificationTitleForCancel(bookingToCancel.getType(), cancelledByOwner),
                NotificationUtil.getNotificationMessageForCancel(
                        bookingToCancel.getType(),
                        bookingToCancel.getStartTime(),
                        bookingToCancel.getEndTime(),
                        cancelledByOwner
                )
        );
        Timeslot timeslot = cacheManager.getCache(bookingToCancel.getTimeSlot());
        if (timeslot != null) {
            timeslot.setBookingCount(timeslot.getBookingCount() - 1);
            cacheManager.putCache(bookingToCancel.getTimeSlot(), timeslot);
        }
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

    @Override
    public List<BookingResponse> getUserBookingsByStatus(BookingStatus status, UUID userId) {
        return bookingRepository.findAllByStatusAndUser(status, userId)
                .stream()
                .map(bookingMapper::mapToBookingResponse)
                .toList();
    }

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

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Slice<Booking> checkBookingsAfterUpdateCache(Pageable pageable, Map<UUID, Timeslot> timeslots) {
        Slice<Booking> bookings = bookingRepository.findAllByStatusIn(List.of(BOOKED, IN_PROGRESS), pageable);
        List<NotificationRequestDto> notifications = new ArrayList<>();
        for (Booking booking : bookings) {
            notifications.add(processBooking(booking, timeslots.get(booking.getTimeSlot())));
        }
        entityManager.flush();
        entityManager.clear();
        notificationSender.sendNotification(notifications);
        return bookings;
    }

    private NotificationRequestDto processBooking(Booking booking, Timeslot newTimeslot) {
        NotificationRequestDto notification = null;
        if (newTimeslot == null) {
            notification = handleMissingTimeslot(booking);
        } else if (!isSameTime(booking, newTimeslot)) {
            notification = handleChangedTimeslot(booking, newTimeslot);
        }

        return notification;
    }

    private NotificationRequestDto handleMissingTimeslot(Booking booking) {
        booking.setStatus(CANCELLED);
        return new NotificationRequestDto(
                booking.getUser(),
                NotificationType.BOOKING,
                NotificationUtil.getNotificationTitleForCancel(booking.getType(), false),
                NotificationUtil.getNotificationMessageForCancel(
                        booking.getType(),
                        booking.getStartTime(),
                        booking.getEndTime(),
                        false
                )
        );
    }

    private NotificationRequestDto handleChangedTimeslot(Booking booking, Timeslot newTimeslot) {
        LocalDateTime oldStart = booking.getStartTime();
        LocalDateTime oldEnd = booking.getEndTime();
        booking.setStartTime(newTimeslot.getStartTime());
        booking.setEndTime(newTimeslot.getEndTime());

        return new NotificationRequestDto(
                booking.getUser(),
                NotificationType.BOOKING,
                NotificationUtil.getNotificationTitleForChangeBooking(booking.getType()),
                NotificationUtil.getNotificationMessageForChangeBooking(
                        booking.getType(),
                        oldStart,
                        oldEnd,
                        newTimeslot.getStartTime(),
                        newTimeslot.getEndTime()
                )
        );
    }

    private boolean isSameTime(Booking booking, Timeslot timeslot) {
        return timeslot.getStartTime().equals(booking.getStartTime())
                && timeslot.getEndTime().equals(booking.getEndTime());
    }

}
