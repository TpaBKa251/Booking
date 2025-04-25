package ru.tpu.hostel.booking.external.amqp.schedule.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "status",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Timeslot.class, name = "SUCCESS"),
        @JsonSubTypes.Type(value = Failure.class, name = "FAILURE")
})
@Getter
@RequiredArgsConstructor
public abstract class ScheduleResponse {

    private final ResponseStatus status;
}