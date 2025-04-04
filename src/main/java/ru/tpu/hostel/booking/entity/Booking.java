package ru.tpu.hostel.booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import ru.tpu.hostel.booking.enums.BookingStatus;
import ru.tpu.hostel.booking.enums.BookingType;
import ru.tpu.hostel.booking.service.impl.states.BookedStateOld;
import ru.tpu.hostel.booking.service.impl.states.CancelStateOld;
import ru.tpu.hostel.booking.service.impl.states.CompletedStateOld;
import ru.tpu.hostel.booking.service.impl.states.InProgressStateOld;
import ru.tpu.hostel.booking.service.impl.states.NotBookedStateOld;
import ru.tpu.hostel.booking.service.state.BookingState;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "booking", schema = "booking")
@Entity
@Getter
@Setter
public class Booking {

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
    private BookingState bookingState;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private BookingType type;

    @Column(name = "\"user\"")
    private UUID user;

    @Column(name = "time_slot_id")
    private UUID timeSlot;

    @PostLoad
    public void initializeBookingState() {
        switch (this.status) {
            case CANCELLED -> this.bookingState = new CancelStateOld();
            case NOT_BOOKED -> this.bookingState = new NotBookedStateOld();
            case BOOKED -> this.bookingState = new BookedStateOld();
            case IN_PROGRESS -> this.bookingState = new InProgressStateOld();
            case COMPLETED -> this.bookingState = new CompletedStateOld();
            default -> throw new IllegalStateException("Unknown booking status: " + this.status);
        }
    }

}
