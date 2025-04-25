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
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;
import ru.tpu.hostel.booking.service.access.CheckOwner;
import ru.tpu.hostel.booking.service.state.BookingState;
import ru.tpu.hostel.booking.service.state.impl.BookedState;
import ru.tpu.hostel.booking.service.state.impl.CancelState;
import ru.tpu.hostel.booking.service.state.impl.CompletedState;
import ru.tpu.hostel.booking.service.state.impl.InProgressState;
import ru.tpu.hostel.booking.service.state.impl.NotBookedState;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Сущность брони
 */
@Table(name = "bookings", schema = "booking")
@Entity
@Getter
@Setter
@ToString(exclude = "bookingState")
@CheckOwner(method = "getUser")
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
            case CANCELLED -> this.bookingState = new CancelState();
            case NOT_BOOKED -> this.bookingState = new NotBookedState();
            case BOOKED -> this.bookingState = new BookedState();
            case IN_PROGRESS -> this.bookingState = new InProgressState();
            case COMPLETED -> this.bookingState = new CompletedState();
            default -> throw new IllegalArgumentException("Неизвестное состояние: " + this.status);
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Booking booking = (Booking) o;
        return getId() != null && Objects.equals(getId(), booking.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy hibernateProxy
                ? hibernateProxy.getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}
