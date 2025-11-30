package ru.tpu.hostel.booking.scheduler;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Scope;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.booking.service.state.BookingState;
import ru.tpu.hostel.internal.utils.TimeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static ru.tpu.hostel.booking.entity.BookingStatus.BOOKED;
import static ru.tpu.hostel.booking.entity.BookingStatus.IN_PROGRESS;

/**
 * Класс для автоматического обновления статусов броней
 */
@Service
@RequiredArgsConstructor
public class BookingStateUpdater {

    private static final String SCOPE_NAME = "ru.tpu.hostel.booking.scheduler.BookingStateUpdater";

    private final BookingRepository bookingRepository;

    private final Map<BookingStatus, BookingState> bookingStates;

    private final OpenTelemetry openTelemetry;

    /**
     * Обновляет статус всех броней каждые 5 минут
     */
    @Scheduled(cron = "0 0/5 * * * ?", zone = "Asia/Tomsk")
    @Transactional
    public void updateBookingStatuses() {
        Span span = createSpan("Update booking statuses");

        try (Scope ignored = span.makeCurrent()) {
            SpanContext context = span.getSpanContext();
            MDC.put("traceId", context.getTraceId());
            MDC.put("spanId", context.getSpanId());

            LocalDateTime now = TimeUtil.now();
            LocalDateTime dayStart = now.toLocalDate().atStartOfDay();
            List<Booking> bookings = bookingRepository.findAllOnDayForUpdateStatus(dayStart, now.plusMinutes(1));
            updateBookingStatuses(bookings);

            span.setStatus(StatusCode.OK);
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
        } finally {
            MDC.clear();
            span.end();
        }
    }

    @Scheduled(cron = "0 30 4 * * *", zone = "Asia/Tomsk")
    public void deleteOldBookings() {
        Span span = createSpan("Delete old bookings");

        try (Scope ignored = span.makeCurrent()) {
            SpanContext context = span.getSpanContext();
            MDC.put("traceId", context.getTraceId());
            MDC.put("spanId", context.getSpanId());

            List<Booking> bookingsForDelete = bookingRepository.findAllForDelete(
                    LocalDate.now().atStartOfDay().minusDays(29)
            );
            updateBookingStatuses(bookingsForDelete);

            span.setStatus(StatusCode.OK);
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
        } finally {
            MDC.clear();
            span.end();
        }
    }

    @Transactional
    public void updateBookingStatusesOnStart() {
        Span span = createSpan("Update booking statuses on start");

        try (Scope ignored = span.makeCurrent()) {
            SpanContext context = span.getSpanContext();
            MDC.put("traceId", context.getTraceId());
            MDC.put("spanId", context.getSpanId());

            List<Booking> bookings = bookingRepository.findAllByStatusIn(List.of(BOOKED, IN_PROGRESS));
            updateBookingStatuses(bookings);

            span.setStatus(StatusCode.OK);
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
        } finally {
            MDC.clear();
            span.end();
        }
    }

    private void updateBookingStatuses(List<Booking> bookings) {
        if (!bookings.isEmpty()) {
            for (Booking booking : bookings) {
                bookingStates.get(booking.getStatus()).updateStatus(booking);
            }
        }
    }

    private Span createSpan(String spanName) {
        return openTelemetry.getTracer(SCOPE_NAME)
                .spanBuilder(spanName)
                .setSpanKind(SpanKind.INTERNAL)
                .startSpan();
    }

}
