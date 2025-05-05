package ru.tpu.hostel.booking.service;

import ru.tpu.hostel.booking.dto.request.BookingTimeSlotRequest;
import ru.tpu.hostel.booking.dto.response.BookingResponse;
import ru.tpu.hostel.booking.dto.response.BookingResponseWithUser;
import ru.tpu.hostel.booking.dto.response.TimeSlotResponse;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.entity.BookingType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Интерфейс сервиса для броней
 */
public interface BookingService {

    /**
     * Создает новую бронь
     *
     * @param bookingTimeSlotRequest ДТО запроса
     * @param userId                 ID юзера
     * @return ДТО-ответ созданной брони
     */
    BookingResponse createBooking(BookingTimeSlotRequest bookingTimeSlotRequest);

    /**
     * Возвращает доступные для брони слоты в виде списка ДТО-ответов
     *
     * @param date        дата брони
     * @param bookingType тип брони
     * @param userId      ID юзера
     * @return список доступных слотов
     */
    List<TimeSlotResponse> getAvailableTimeslotsForBooking(LocalDate date, BookingType bookingType, UUID userId);

    /**
     * Закрывает бронь по ID
     *
     * @param bookingId ID брони
     * @param userId    ID юзера
     * @return ДТО-ответ закрытой брони
     */
    BookingResponse cancelBooking(UUID bookingId);

    /**
     * Возвращает список из ДТО-ответов броней по статусу для юзера
     *
     * @param status статус брони
     * @param userId ID юзера
     * @return список ДТО-ответов броней
     */
    List<BookingResponse> getUserBookingsByStatus(BookingStatus status, UUID userId);

    /**
     * Возвращает все брони юзера в виде ДТО-ответов
     *
     * @param userId ID юзера
     * @return список ДТО-ответов броней
     */
    List<BookingResponse> getBookingsByUser(UUID userId);

    /**
     * Возвращает список из ДТО-ответов с ID юзера для всех броней определенного типа на конкретную дату
     *
     * @param bookingType тип брони
     * @param date        дата брони
     * @return список ДТО-ответов броней
     */
    List<BookingResponseWithUser> getBookingsByTypeAndDateWithUser(BookingType bookingType, LocalDate date);

    /**
     * Возвращает список из ДТО-ответов с ID юзера для всех броней на конкретную дату
     *
     * @param date дата брони
     * @return список ДТО-ответов броней
     */
    List<BookingResponseWithUser> getBookingsByDateWithUser(LocalDate date);

}
