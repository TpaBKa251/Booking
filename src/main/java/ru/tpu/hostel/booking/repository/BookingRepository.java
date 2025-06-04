package ru.tpu.hostel.booking.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.BookingStatus;
import ru.tpu.hostel.booking.entity.BookingType;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для броней
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    /**
     * Ищет все брони, принадлежащие юзеру
     *
     * @param user ID юзера
     * @return список найденных броней
     */
    @Query(value = """
            SELECT b
            FROM Booking b
            WHERE b.user = :user
            """
    )
    List<Booking> findAllByUser(@Param("user") UUID user);

    /**
     * Ищет все брони, принадлежащие юзеру с указанным статусом
     *
     * @param status статус брони
     * @param user   ID юзера
     * @return список найденных броней
     */
    @Query(value = """
            SELECT b
            FROM Booking b
            WHERE b.status = :status
                AND b.user = :user
            """
    )
    @QueryHints({@QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "true")})
    List<Booking> findAllByStatusAndUser(@Param("status") BookingStatus status, @Param("user") UUID user);

    /**
     * Ищет конкретную бронь, принадлежащую юзеру с указанным статусом и ID таймслота
     *
     * @param timeSlot ID таймслота
     * @param user     ID юзера
     * @param status   статус брони
     * @return найденную бронь
     */
    @Query(value = """
            SELECT b
            FROM Booking b
            WHERE b.status = :status
                AND b.user = :user
                AND b.timeSlot = :timeSlot
            """
    )
    Optional<Booking> findByTimeSlotAndUserAndStatus(
            @Param("timeSlot") UUID timeSlot,
            @Param("user") UUID user,
            @Param("status") BookingStatus status
    );

    @Query(value = """
            SELECT b
            FROM Booking b
            WHERE b.type = :type
                AND b.startTime >= :dayStart
                AND b.startTime < :dayEnd
                AND b.status = 'BOOKED'
            """
    )
    List<Booking> findAllBookedBookingsByTypeAndStartTimeOnSpecificDay(
            @Param("type") BookingType type,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd
    );

    @Query(value = """
            SELECT b FROM Booking b
            WHERE b.status = 'BOOKED'
                AND b.startTime >= :dayStart
                AND b.startTime < :dayEnd
            """
    )
    List<Booking> findAllBookedBookingsOnSpecificDay(
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd
    );

    @Query(value = """
            SELECT b
            FROM Booking b
            WHERE b.timeSlot IN (:timeSlots)
            """
    )
    List<Booking> findAllByTimeSlotIn(@Param("timeSlots") Collection<UUID> timeSlots);

    @Query(value = """
            SELECT b
            FROM Booking b
            WHERE b.id = :id
            """
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Booking> findByIdForUpdate(@Param("id") UUID id);

    @Query(value = """
            SELECT b
            FROM Booking b
            WHERE "user" = :user
                AND b.timeSlot = :timeSlot
                AND b.status = 'BOOKED'
            """
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Booking> findByUserAndTimeSlotForUpdate(@Param("user") UUID user, @Param("timeSlot") UUID timeSlot);

    @Query(value = """
            SELECT EXISTS(SELECT 1
                            FROM Booking b
                            WHERE b.timeSlot = :timeslotId
                                AND b.user = :userId
                                AND b.status = 'BOOKED')
            """
    )
    boolean existsByTimeSlotAndUser(@Param("timeslotId") UUID timeslotId, @Param("userId") UUID userId);

    @Query(value = """
            SELECT b.timeSlot FROM Booking b
            WHERE b.user = :user
                AND b.status = 'BOOKED'
                AND b.startTime >= :dayStart
                AND b.startTime < :dayEnd
            """
    )
    @QueryHints({@QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "true")})
    List<UUID> findAllBookedTimeslotIdsByUser(
            @Param("user") UUID user,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd
    );

    @Query(value = """
            SELECT b
            FROM Booking b
            WHERE b.startTime >= :dayStart
                AND b.startTime <= :time
                AND b.status IN ('BOOKED', 'IN_PROGRESS')
            """
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Booking> findAllOnDayForUpdateStatus(
            @Param("dayStart") LocalDateTime dayStart,
            @Param("time") LocalDateTime time
    );

    @Query(value = """
            SELECT b
            FROM Booking b
            WHERE b.status IN (:statuses)
            """
    )
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Booking> findAllByStatusIn(@Param("statuses") Collection<BookingStatus> statuses);

}
