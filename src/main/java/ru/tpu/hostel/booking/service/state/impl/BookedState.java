package ru.tpu.hostel.booking.service.state.impl;

import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.booking.service.state.BookingState;
import ru.tpu.hostel.internal.utils.TimeUtil;

/**
 * Реализация интерфейса {@link BookingState} для состояния "Забронировано"
 */
@Service
public class BookedState implements BookingState {

    @Override
    public void updateStatus(Booking booking, BookingRepository bookingRepository) {
//        long minutesBetween = Duration.between(booking.getStartTime(), TimeUtil.now()).toMinutes();
//        if (minutesBetween >= 14 && minutesBetween <= 15) {
//            notificationSender.sendNotification(
//                    booking.getUser(),
//                    NotificationType.BOOKING,
//                    NotificationTimeUtil.getNotificationTitleForStartBooking(booking.getType()),
//                    NotificationTimeUtil.getNotificationMessageForStartBooking(booking.getType())
//            );
//        }
        if (booking.getStartTime().isBefore(TimeUtil.now())) {
            booking.setStatus(BookingStatus.IN_PROGRESS);
            booking.setBookingState(new InProgressState());

            booking.getBookingState().updateStatus(booking, bookingRepository);
            if (booking.getStatus() == BookingStatus.IN_PROGRESS) {
                bookingRepository.save(booking);
            }
        }
    }

    @Override
    public void cancelBooking(Booking booking, BookingRepository bookingRepository) {
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }
}
