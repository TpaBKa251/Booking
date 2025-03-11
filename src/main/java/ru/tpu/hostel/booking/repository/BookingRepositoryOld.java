package ru.tpu.hostel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tpu.hostel.booking.entity.BookingOld;
import ru.tpu.hostel.booking.entity.TimeSlot;
import ru.tpu.hostel.booking.enums.BookingStatus;
import ru.tpu.hostel.booking.enums.BookingType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Этот класс устарел и будет удалён в будущем.
 * Вместо него используйте {@link BookingRepository}.
 * @deprecated Класс заменён на {@link BookingRepository}.
 *
 * @see BookingRepository
 */
@SuppressWarnings("removal")
@Deprecated(forRemoval = true)
@Repository
public interface BookingRepositoryOld extends JpaRepository<BookingOld, UUID> {

    List<BookingOld> findAllByStatus(BookingStatus status);

    List<BookingOld> findAllByType(BookingType type);

    List<BookingOld> findAllByStatusNotAndTimeSlot(BookingStatus status, TimeSlot timeSlot);

    List<BookingOld> findAllByStatusAndType(BookingStatus status, BookingType type);

    List<BookingOld> findAllByTimeSlot(TimeSlot timeSlot);

    List<BookingOld> findAllByUser(UUID user);

    List<BookingOld> findAllByStatusAndUser(BookingStatus status, UUID user);

    Optional<BookingOld> findByTimeSlotAndUserAndStatus(TimeSlot timeSlot, UUID user, BookingStatus status);

    @Query("SELECT b FROM BookingOld b WHERE b.type = :type AND b.startTime >= :startDate AND b.startTime < :endDate AND b.status = 'BOOKED'")
    List<BookingOld> findAllByTypeAndStartTimeOnSpecificDay(
            @Param("type") BookingType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT b FROM BookingOld b WHERE b.startTime >= :startDate AND b.startTime < :endDate AND b.status = 'BOOKED'")
    List<BookingOld> findAllBookingsOnSpecificDay(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}
