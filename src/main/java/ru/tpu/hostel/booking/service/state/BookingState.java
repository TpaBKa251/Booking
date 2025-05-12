package ru.tpu.hostel.booking.service.state;

import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.repository.BookingRepository;
import ru.tpu.hostel.internal.common.logging.SecretArgument;

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
    void updateStatus(Booking booking, @SecretArgument BookingRepository bookingRepository);

    /**
     * Закрывает бронь
     *
     * @param booking           бронь
     * @param bookingRepository репозиторий броней
     */
    void cancelBooking(Booking booking, @SecretArgument BookingRepository bookingRepository);

}