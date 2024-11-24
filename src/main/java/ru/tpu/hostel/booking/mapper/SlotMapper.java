package ru.tpu.hostel.booking.mapper;

import org.springframework.stereotype.Component;
import ru.tpu.hostel.booking.dto.response.TimeSlotResponseDto;
import ru.tpu.hostel.booking.entity.TimeSlot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class SlotMapper {

    public static TimeSlotResponseDto mapTimeSlotToTimeSlotResponseDto(TimeSlot timeSlot) {
        return new TimeSlotResponseDto(
                timeSlot.getId(),
                formatTime(timeSlot.getStartTime()) + "-" + formatTime(timeSlot.getEndTime())
        );
    }

    private static String formatTime(LocalDateTime dateTime) {
        return dateTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
