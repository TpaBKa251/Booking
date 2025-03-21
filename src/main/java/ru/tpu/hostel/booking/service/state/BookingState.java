package ru.tpu.hostel.booking.service.state;

import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.repository.BookingRepository;

/**
 * Интерфейс состояния брони
 */
public interface BookingState {

    /**
     * Обновляет состояние брони
     *
     * @param booking           бронь
     * @param bookingRepository репозиторий броней
     */
    void updateStatus(Booking booking, BookingRepository bookingRepository);

    /**
     * Закрывает бронь
     *
     * @param booking           бронь
     * @param bookingRepository репозиторий броней
     */
    void cancelBooking(Booking booking, BookingRepository bookingRepository);

}