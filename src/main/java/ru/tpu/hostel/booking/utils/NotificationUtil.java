package ru.tpu.hostel.booking.utils;

import lombok.experimental.UtilityClass;
import ru.tpu.hostel.booking.entity.BookingType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class NotificationUtil {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM");

    private static final String BOOK_TITLE = "Вы записаны в %s";

    private static final String BOOK_MESSAGE = BOOK_TITLE + " на %s в %s.";

    private static final String CANCEL_OWNER_TITLE = "Запись в %s отменена";

    private static final String CANCEL_OWNER_MESSAGE = "Запись в %s на %s в %s отменена.";

    private static final String CANCEL_TITLE = "Вашу запись в %s отменили";

    private static final String CANCEL_MESSAGE = "Запись в %s на %s в %s отменена ответственным за ресурс.";

    private static final String START_BOOKING_TITLE = "Ваша запись в %s скоро начнется";

    private static final String START_BOOKING_MESSAGE = "Ваша запись в %s начнется через 15 минут.";

    public static String getNotificationTitleForBook(BookingType type) {
        return BOOK_TITLE.formatted(type.getBookingTypeName());
    }

    public static String getNotificationMessageForBook(BookingType type, LocalDateTime start, LocalDateTime end) {
        return BOOK_MESSAGE.formatted(
                type.getBookingTypeName(),
                start.toLocalDate().format(DATE_FORMATTER),
                getTimeRange(start, end)
        );
    }

    public static String getNotificationTitleForCancel(BookingType type, boolean cancelledByOwner) {
        return cancelledByOwner
                ? CANCEL_OWNER_TITLE.formatted(type.getBookingTypeName())
                : CANCEL_TITLE.formatted(type.getBookingTypeName());
    }

    public static String getNotificationMessageForCancel(
            BookingType type,
            LocalDateTime start,
            LocalDateTime end,
            boolean cancelledByOwner
    ) {
        return cancelledByOwner
                ? CANCEL_OWNER_MESSAGE.formatted(
                type.getBookingTypeName(),
                start.toLocalDate().format(DATE_FORMATTER),
                getTimeRange(start, end))
                : CANCEL_MESSAGE.formatted(
                type.getBookingTypeName(),
                start.toLocalDate().format(DATE_FORMATTER),
                getTimeRange(start, end));
    }

    public static String getNotificationTitleForStartBooking(BookingType type) {
        return START_BOOKING_TITLE.formatted(type.getBookingTypeName());
    }

    public static String getNotificationMessageForStartBooking(BookingType type) {
        return START_BOOKING_MESSAGE.formatted(
                type.getBookingTypeName()
        );
    }

    private static String getTimeRange(LocalDateTime start, LocalDateTime end) {
        return start.format(TIME_FORMATTER) + "-" + end.format(TIME_FORMATTER);
    }
}