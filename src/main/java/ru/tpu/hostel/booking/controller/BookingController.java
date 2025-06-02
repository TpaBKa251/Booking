package ru.tpu.hostel.booking.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.booking.dto.request.BookingTimeSlotRequest;
import ru.tpu.hostel.booking.dto.response.BookingResponse;
import ru.tpu.hostel.booking.dto.response.BookingResponseWithUser;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.entity.BookingType;
import ru.tpu.hostel.booking.service.BookingService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Контроллер для броней
 */
@RestController
@RequestMapping("bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    /**
     * Бронирует таймслот
     *
     * @param bookingTimeSlotRequestDto ДТО с ID слота
     * @param userId                    ID юзера, который делает бронь
     * @return ДТО-ответ с информацией о брони
     */
    @PostMapping
    public BookingResponse book(@RequestBody @Valid BookingTimeSlotRequest bookingTimeSlotRequestDto) {
        return bookingService.createBooking(bookingTimeSlotRequestDto);
    }

    /**
     * Закрывает бронь
     *
     * @param bookingId ID брони
     * @param userId    ID юзера, который закрывает бронь
     * @return ДТО-ответ с информацией о брони
     */
    @PatchMapping("/cancel/{bookingId}")
    public BookingResponse cancel(@PathVariable UUID bookingId) {
        return bookingService.cancelBooking(bookingId);
    }

    @PatchMapping("/cancel/by-timeslot/{timeslotId}")
    public BookingResponse cancelByTimeSlot(@PathVariable UUID timeslotId) {
        return bookingService.cancelBookingByTimeslot(timeslotId);
    }

    /**
     * Возвращает список броней пользователя с указанным статусом
     *
     * @param status статус брони
     * @param userId ID юзера
     * @return список ДТО-ответов с информацией о бронях
     */
    @GetMapping("get/all/by/status/user/{status}/{userId}")
    public List<BookingResponse> getAllByStatus(@PathVariable BookingStatus status, @PathVariable UUID userId) {
        return bookingService.getUserBookingsByStatus(status, userId);
    }

    @GetMapping("all/booked/timeslot-id")
    public List<UUID> getAllByStatusShort(
            @RequestParam(name = "userId") UUID userId,
            @RequestParam(name = "date") LocalDate date
    ) {
        return bookingService.getUserBookingsByStatusShort(userId, date);
    }

    /**
     * Возвращает все брони юзера
     *
     * @param userId ID юзера
     * @return список ДТО-ответов с информацией о бронях
     */
    @GetMapping("get/all/by/user/{userId}")
    public List<BookingResponse> getAllByUserId(@PathVariable UUID userId) {
        return bookingService.getBookingsByUser(userId);
    }

    /**
     * Возвращает все брони по типу на конкретную дату с ID юзера
     *
     * @param type тип брони
     * @param date дата
     * @return список ДТО-ответов с информацией о бронях с ID юзера
     */
    @GetMapping("get/all/by/type/date/{type}/{date}")
    public List<BookingResponseWithUser> getAllByTypeAndDate(
            @PathVariable BookingType type,
            @PathVariable LocalDate date
    ) {
        return bookingService.getBookingsByTypeAndDateWithUser(type, date);
    }

    /**
     * Возвращает все брони на конкретную дату с ID юзера
     *
     * @param date дата
     * @return список ДТО-ответов с информацией о бронях с ID юзера
     */
    @GetMapping("get/all/by/date/{date}")
    public List<BookingResponseWithUser> getAllByDate(@PathVariable LocalDate date) {
        return bookingService.getBookingsByDateWithUser(date);
    }

}
