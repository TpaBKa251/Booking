package ru.tpu.hostel.booking.service.state.impl;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Scope;
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

    private static final String SCOPE_NAME = "ru.tpu.hostel.booking.service.state.update";

    private final BookingState inProgressState;

    private final NotificationSender notificationSender;

    private final OpenTelemetry openTelemetry;

    @Override
    public void updateStatus(Booking booking) {
        if (booking.getStartTime().isBefore(TimeUtil.now())) {
            booking.setStatus(BookingStatus.IN_PROGRESS);
            inProgressState.updateStatus(booking);
        }

        long minutesBetween = Duration.between(booking.getStartTime(), TimeUtil.now()).toMinutes();
        if (booking.getStatus() == BookingStatus.BOOKED && minutesBetween >= 14 && minutesBetween <= 15) {
            Span span = openTelemetry.getTracer(SCOPE_NAME).spanBuilder("Remind booking")
                    .setSpanKind(SpanKind.CLIENT)
                    .setAttribute("notification.type", "BOOKING")
                    .setAttribute("booking.minutes_before", minutesBetween)
                    .setAttribute("booking.id", booking.getId().toString())
                    .startSpan();
            try (Scope ignored = span.makeCurrent()) {
                notificationSender.sendNotification(
                        booking.getUser(),
                        NotificationType.BOOKING,
                        NotificationUtil.getNotificationTitleForStartBooking(booking.getType()),
                        NotificationUtil.getNotificationMessageForStartBooking(booking.getType())
                );
                span.setStatus(StatusCode.OK);
            } catch (Exception e) {
                span.setStatus(StatusCode.ERROR);
                span.recordException(e);
            } finally {
                span.end();
            }
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
