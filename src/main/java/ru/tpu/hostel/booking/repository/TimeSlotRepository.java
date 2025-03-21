package ru.tpu.hostel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tpu.hostel.booking.entity.TimeSlot;
import ru.tpu.hostel.booking.entity.BookingType;

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

    //Здесь ищется конкретный слот на какое-то время (или в промежутке?)
    @Query("SELECT t.startTime FROM TimeSlot t WHERE t.type = :type AND t.startTime >= :startDate AND t.startTime < :endDate order by t.startTime limit 1")
    Optional<LocalDateTime> findOneByTypeAndStartTimeOnSpecificDay(
            @Param("type") BookingType type,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
