package ru.tpu.hostel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.entity.TimeSlot;
import ru.tpu.hostel.booking.enums.BookingStatus;
import ru.tpu.hostel.booking.enums.BookingType;

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

    Optional<Booking> findByTimeSlotAndUser(TimeSlot timeSlot, UUID user);
}
