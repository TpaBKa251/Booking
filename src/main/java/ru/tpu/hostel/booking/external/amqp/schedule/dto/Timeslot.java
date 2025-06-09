package ru.tpu.hostel.booking.external.amqp.schedule.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.tpu.hostel.booking.entity.BookingType;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Getter
public class Timeslot extends ScheduleResponse {

    private final UUID id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private final LocalDateTime endTime;

    private final BookingType type;

    private final Integer limit;

    @Setter
    private Integer bookingCount;

    @JsonCreator
    public Timeslot(
            @JsonProperty("id") UUID id,
            @JsonProperty("startTime") LocalDateTime startTime,
            @JsonProperty("endTime") LocalDateTime endTime,
            @JsonProperty("type") BookingType type,
            @JsonProperty("limit") Integer limit,
            @JsonProperty("bookingCount") Integer bookingCount
    ) {
        super(ResponseStatus.SUCCESS);
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.limit = limit;
        this.bookingCount = bookingCount;
    }

    public boolean isAvailable() {
        return bookingCount.compareTo(limit) < 0;
    }
}

