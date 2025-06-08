package ru.tpu.hostel.booking.service.state.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.service.state.BookingState;
import ru.tpu.hostel.booking.utils.NotificationUtil;
import ru.tpu.hostel.internal.external.amqp.dto.NotificationType;
import ru.tpu.hostel.internal.service.NotificationSender;
import ru.tpu.hostel.internal.utils.TimeUtil;

import java.time.Duration;

/**
 * Реализация интерфейса {@link BookingState} для состояния "Забронировано"
 */
@Service
@RequiredArgsConstructor
public class BookedState implements BookingState {

    private final BookingState inProgressState;

    private final NotificationSender notificationSender;

    @Override
    public void updateStatus(Booking booking) {
        if (booking.getStartTime().isBefore(TimeUtil.now())) {
            booking.setStatus(BookingStatus.IN_PROGRESS);
            inProgressState.updateStatus(booking);
        }

        long minutesBetween = Duration.between(booking.getStartTime(), TimeUtil.now()).toMinutes();
        if (booking.getStatus() == BookingStatus.BOOKED && minutesBetween >= 14 && minutesBetween <= 15) {
            notificationSender.sendNotification(
                    booking.getUser(),
                    NotificationType.BOOKING,
                    NotificationUtil.getNotificationTitleForStartBooking(booking.getType()),
                    NotificationUtil.getNotificationMessageForStartBooking(booking.getType())
            );
        }
    }

    @Override
    public void cancelBooking(Booking booking) {
        booking.setStatus(BookingStatus.CANCELLED);
    }

    @Override
    public BookingStatus getStatus() {
        return BookingStatus.BOOKED;
    }
}
