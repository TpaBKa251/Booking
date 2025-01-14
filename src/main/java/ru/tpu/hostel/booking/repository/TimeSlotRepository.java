package ru.tpu.hostel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.tpu.hostel.booking.entity.TimeSlot;
import ru.tpu.hostel.booking.enums.BookingType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, UUID> {

    @Query("select t from TimeSlot t where t.type = :bookingType order by t.startTime desc limit 1")
    Optional<TimeSlot> findLastByType(BookingType bookingType);

    List<TimeSlot> findByType(BookingType bookingType);

    List<TimeSlot> findAllByTypeAndStartTimeAfter(BookingType bookingType, LocalDateTime startTime);
}
