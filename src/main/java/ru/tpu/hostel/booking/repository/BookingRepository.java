package ru.tpu.hostel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.TimeSlot;
import ru.tpu.hostel.booking.enums.BookingStatus;
import ru.tpu.hostel.booking.enums.BookingType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    List<Booking> findAllByStatus(BookingStatus status);

    List<Booking> findAllByType(BookingType type);

    List<Booking> findAllByStatusNotAndTimeSlot(BookingStatus status, TimeSlot timeSlot);

    List<Booking> findAllByStatusAndType(BookingStatus status, BookingType type);

    List<Booking> findAllByTimeSlot(TimeSlot timeSlot);

    List<Booking> findAllByUser(UUID user);

    List<Booking> findAllByStatusAndUser(BookingStatus status, UUID user);

    Optional<Booking> findByTimeSlotAndUserAndStatus(TimeSlot timeSlot, UUID user, BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.type = :type AND b.startTime >= :startDate AND b.startTime < :endDate AND b.status = 'BOOKED'")
    List<Booking> findAllByTypeAndStartTimeOnSpecificDay(
            @Param("type") BookingType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT b FROM Booking b WHERE b.startTime >= :startDate AND b.startTime < :endDate AND b.status = 'BOOKED'")
    List<Booking> findAllBookingsOnSpecificDay(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}
