package ru.tpu.hostel.booking.external.amqp.schedule.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
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

    @JsonCreator
    public Timeslot(
            @JsonProperty("id") UUID id,
            @JsonProperty("startTime") LocalDateTime startTime,
            @JsonProperty("endTime") LocalDateTime endTime,
            @JsonProperty("type") BookingType type
    ) {
        super(ResponseStatus.SUCCESS);
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
    }
}

