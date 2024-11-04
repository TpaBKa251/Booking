package ru.tpu.hostel.booking.mapper;

import org.springframework.stereotype.Component;
import ru.tpu.hostel.booking.dto.response.TimeSlotResponseDto;
import ru.tpu.hostel.booking.entity.TimeSlot;

@Component
public class SlotMapper {

    public static TimeSlotResponseDto mapTimeSlotToTimeSlotResponseDto(TimeSlot timeSlot) {
        return new TimeSlotResponseDto(
                timeSlot.getId(),
                timeSlot.getStartTime(),
                timeSlot.getEndTime()
        );
    }
}
