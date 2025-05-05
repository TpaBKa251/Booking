package ru.tpu.hostel.booking.service.old.schedules;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Deprecated(forRemoval = true)
@Setter
@Getter
public class SchedulesConfig {

    private Map<String, Schedule> schedules;

    @Setter
    @Getter
    public static class Schedule {
        private String type;
        private int limit;
        private UUID responsible;
        private List<String> workingDays;
        private TimeRange workingHours;
        private int slotDurationMinutes;
        private BreakConfig breaks;
        private Map<String, List<TimeRange>> reservedHours;
    }

    @Setter
    @Getter
    public static class TimeRange {
        private LocalTime start;
        private LocalTime end;
        private boolean endNextDay;
    }

    @Setter
    @Getter
    public static class BreakConfig {
        private int afterSlots;
        private int breakDurationMinutes;
    }

    public static SchedulesConfig loadFromFile(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper.readValue(new File(filePath), SchedulesConfig.class);
    }
}
