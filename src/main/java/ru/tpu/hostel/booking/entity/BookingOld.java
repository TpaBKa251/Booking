package ru.tpu.hostel.booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.tpu.hostel.booking.service.old.state.BookedStateOld;
import ru.tpu.hostel.booking.service.old.state.BookingStateOld;
import ru.tpu.hostel.booking.service.old.state.CancelStateOld;
import ru.tpu.hostel.booking.service.old.state.CompletedStateOld;
import ru.tpu.hostel.booking.service.old.state.InProgressStateOld;
import ru.tpu.hostel.booking.service.old.state.NotBookedStateOld;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Этот класс устарел и будет удалён в будущем.
 * Вместо него используйте {@link Booking}.
 * @deprecated Класс заменён на {@link Booking}.
 *
 * @see Booking
 */
@Deprecated(forRemoval = true)
@Table(name = "booking", schema = "booking") // Указание схемы
@Entity
@Getter
@Setter
@ToString(exclude = {"timeSlot", "bookingState"})
public class BookingOld {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, name = "start_time")
    private LocalDateTime startTime;

    @Column(nullable = false, name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;

    @Transient
    private BookingStateOld bookingState;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private BookingType type;

    @Column(name = "\"user\"")
    private UUID user;

    @ManyToOne
    @JoinColumn(name = "time_slot_id", referencedColumnName = "id") // Ссылка на TimeSlot
    private TimeSlot timeSlot;

    @PostLoad
    public void initializeBookingState() {
        switch (this.status) {
            case CANCELLED -> this.bookingState = new CancelStateOld();
            case NOT_BOOKED -> this.bookingState = new NotBookedStateOld();
            case BOOKED -> this.bookingState = new BookedStateOld();
            case IN_PROGRESS -> this.bookingState = new InProgressStateOld();
            case COMPLETED -> this.bookingState = new CompletedStateOld();
            default -> throw new IllegalArgumentException("Неизвестное состояние: " + this.status);
        }
    }
}
