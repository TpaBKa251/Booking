package ru.tpu.hostel.booking.service.state.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.cache.RedissonListCacheManager;
import ru.tpu.hostel.booking.dto.response.BookingResponse;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.service.state.BookingState;
import ru.tpu.hostel.booking.utils.NotificationUtil;
import ru.tpu.hostel.internal.external.amqp.dto.NotificationType;
import ru.tpu.hostel.internal.service.NotificationSender;
import ru.tpu.hostel.internal.utils.TimeUtil;

import java.time.Duration;
import java.util.UUID;

import static ru.tpu.hostel.booking.config.cache.RedissonCacheConfig.KEY_PATTERN;
import static ru.tpu.hostel.booking.entity.BookingStatus.BOOKED;

/**
 * Реализация интерфейса {@link BookingState} для состояния "Забронировано"
 */
@Service
@RequiredArgsConstructor
public class BookedState implements BookingState {

    private final BookingState inProgressState;

    private final NotificationSender notificationSender;

    private final RedissonListCacheManager<String, BookingResponse> cacheManager;

    @Override
    public void updateStatus(Booking booking) {
        if (booking.getStartTime().isBefore(TimeUtil.now())) {
            booking.setStatus(BookingStatus.IN_PROGRESS);
            updateCache(booking.getId(), booking.getUser());
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

    private void updateCache(UUID id, UUID userId) {
        try {
            cacheManager.removeCache(KEY_PATTERN.formatted(userId, BOOKED), id);
        } catch (Exception ignored) {
            // Ничего не делаем
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
