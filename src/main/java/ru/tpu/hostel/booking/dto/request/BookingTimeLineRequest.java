package ru.tpu.hostel.booking.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import ru.tpu.hostel.booking.entity.BookingType;

import java.time.LocalDateTime;

@Deprecated(forRemoval = true)
public record BookingTimeLineRequest(

        @NotNull(message = "Тип ресурса для брони не может быть пустым")
        BookingType bookingType,

        @NotNull(message = "Стартовая дата не может быть пустой")
        @Future(message = "Стартовая дата не может быть в прошлом")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime startTime,

        @NotNull(message = "Конечная дата не может быть пустой")
        @Future(message = "Конечная дата не может быть в прошлом")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime endTime
) {
}
