package ru.tpu.hostel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.entity.BookingType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для броней
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    /**
     * Ищет все брони с указанным статусом и ID таймслота
     *
     * @param status   статус брони
     * @param timeSlot ID таймслота
     * @return список найденных броней
     */
    List<Booking> findAllByStatusNotAndTimeSlot(BookingStatus status, UUID timeSlot);

    /**
     * Ищет все брони, принадлежащие юзеру
     *
     * @param user ID юзера
     * @return список найденных броней
     */
    List<Booking> findAllByUser(UUID user);

    /**
     * Ищет все брони, принадлежащие юзеру с указанным статусом
     *
     * @param status статус брони
     * @param user   ID юзера
     * @return список найденных броней
     */
    List<Booking> findAllByStatusAndUser(BookingStatus status, UUID user);

    /**
     * Ищет конкретную бронь, принадлежащую юзеру с указанным статусом и ID таймслота
     *
     * @param timeSlot ID таймслота
     * @param user     ID юзера
     * @param status   статус брони
     * @return найденную бронь
     */
    Optional<Booking> findByTimeSlotAndUserAndStatus(UUID timeSlot, UUID user, BookingStatus status);

    /**
     * Ищет все брони определенного типа в указанный день и в статусе "Забронировано" (BOOKED)
     *
     * @param type тип брони
     * @param day  день
     * @return список найденных броней
     */
    @Query("""
            SELECT b FROM BookingOld b
            WHERE b.type = :type
                AND FUNCTION('DATE_TRUNC', 'day', b.startTime) = :day
                AND b.status = 'BOOKED'
            """)
    List<Booking> findAllBookedBookingsByTypeAndStartTimeOnSpecificDay(
            @Param("type") BookingType type,
            @Param("day") LocalDate day
    );

    /**
     * Ищет все брони в указанный день и в статусе "Забронировано" (BOOKED)
     *
     * @param day день
     * @return список найденных броней
     */
    @Query("""
            SELECT b FROM BookingOld b
            WHERE b.status = 'BOOKED'
                AND FUNCTION('DATE_TRUNC', 'day', b.startTime) = :day
            """)
    List<Booking> findAllBookedBookingsOnSpecificDay(@Param("day") LocalDate day);

}
