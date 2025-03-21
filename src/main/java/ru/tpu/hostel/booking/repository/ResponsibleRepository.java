package ru.tpu.hostel.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.tpu.hostel.booking.entity.Responsible;
import ru.tpu.hostel.booking.entity.BookingType;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResponsibleRepository extends JpaRepository<Responsible, UUID> {

    Optional<Responsible> findByTypeAndDate(BookingType type, LocalDate date);

    //я карта я карта я карта я карта я карта я карта я карта я карта я карта я карта я карта
    @Query("select r.user from Responsible r where r.type = :type and r.date = :date")
    Optional<UUID> findUserByTypeAndDate(@Param("type") BookingType type, @Param("date") LocalDate date);
}
