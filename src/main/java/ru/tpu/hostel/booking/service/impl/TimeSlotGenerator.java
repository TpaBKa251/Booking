package ru.tpu.hostel.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.entity.TimeSlot;
import ru.tpu.hostel.booking.enums.BookingType;
import ru.tpu.hostel.booking.repository.TimeSlotRepository;
import ru.tpu.hostel.booking.service.schedules.SchedulesConfig;
import ru.tpu.hostel.booking.utils.TimeNow;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class TimeSlotGenerator {

    private static final String FILE_PATH = System.getenv("SCHEDULES_FILE_PATH");

    private final TimeSlotRepository timeSlotRepository;

    @Bean
    public ApplicationRunner initializeGymTimeSlots() {
        return args -> generateSlotsForWeek();
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Tomsk")
    public void generateSlotsOnLastDay() {
        SchedulesConfig config;

        try {
            config = SchedulesConfig.loadFromFile(FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки шаблона расписаний", e);
        }

        List<TimeSlot> slots = new ArrayList<>();

        for (SchedulesConfig.Schedule schedule : config.getSchedules().values()) {
            List<DayOfWeek> workingDays = parseWorkingDays(schedule.getWorkingDays());
            Map<String, List<SchedulesConfig.TimeRange>> reservedHours = schedule.getReservedHours();

            LocalDate today = TimeNow.now().toLocalDate();

            LocalDate currentDate = today.plusDays(7);
            DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek();

            if (workingDays.contains(currentDayOfWeek)) {
                List<TimeSlot> dailySlots = generateDailySlots(
                        currentDate,
                        reservedHours.get(currentDayOfWeek.name()),
                        schedule
                );

                slots.addAll(dailySlots);
            }
        }

        timeSlotRepository.saveAll(slots);
    }

    public void generateSlotsForWeek() {
        SchedulesConfig config;

        try {
            config = SchedulesConfig.loadFromFile(FILE_PATH);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки шаблона расписаний", e);
        }

        List<TimeSlot> slots = new ArrayList<>();
        LocalDate endOfWeek = TimeNow.now().toLocalDate().plusDays(8);

        for (SchedulesConfig.Schedule schedule : config.getSchedules().values()) {
            List<DayOfWeek> workingDays = parseWorkingDays(schedule.getWorkingDays());
            Map<String, List<SchedulesConfig.TimeRange>> reservedHours = schedule.getReservedHours();

            TimeSlot lastSlot = timeSlotRepository.findLastByType(BookingType.valueOf(schedule.getType())).orElse(null);

            LocalDate currentDate = (lastSlot == null || lastSlot.getStartTime().toLocalDate().isBefore(TimeNow.now().toLocalDate()))
                    ? TimeNow.now().toLocalDate()
                    : lastSlot.getStartTime().toLocalDate().plusDays(1);

            while (currentDate.isBefore(endOfWeek)) {
                DayOfWeek currentDayOfWeek = currentDate.getDayOfWeek();

                if (workingDays.contains(currentDayOfWeek)) {
                    List<TimeSlot> dailySlots = generateDailySlots(
                            currentDate,
                            reservedHours.get(currentDayOfWeek.name()),
                            schedule
                    );

                    slots.addAll(dailySlots);
                }

                currentDate = currentDate.plusDays(1);
            }
        }

        timeSlotRepository.saveAll(slots);
    }

    private List<TimeSlot> generateDailySlots(
            LocalDate date,
            List<SchedulesConfig.TimeRange> reservedHours,
            SchedulesConfig.Schedule schedule
    ) {
        String type = schedule.getType();
        int limit = schedule.getLimit();
        LocalTime startTime = schedule.getWorkingHours().getStart();
        LocalTime endTime = schedule.getWorkingHours().getEnd();
        boolean endNextDay = schedule.getWorkingHours().isEndNextDay();
        int slotDuration = schedule.getSlotDurationMinutes();
        SchedulesConfig.BreakConfig breaks = schedule.getBreaks();

        List<TimeSlot> dailySlots = new ArrayList<>();
        LocalDateTime slotStart = LocalDateTime.of(date, startTime);
        LocalDateTime workingEnd = endNextDay
                ? LocalDateTime.of(date.plusDays(1), endTime)
                : LocalDateTime.of(date, endTime);
        int slotsCounter = 0;

        while (slotStart.plusMinutes(slotDuration).isBefore(workingEnd) || slotStart.plusMinutes(slotDuration).equals(workingEnd)) {
            LocalDateTime slotEnd = slotStart.plusMinutes(slotDuration);

            LocalDateTime finalSlotStart = slotStart;
            boolean overlapsReserved = reservedHours != null && reservedHours.stream()
                    .anyMatch(reserved -> isOverlapping(finalSlotStart.toLocalTime(), slotEnd.toLocalTime(), reserved));

            if (!overlapsReserved) {
                TimeSlot timeSlot = new TimeSlot();
                timeSlot.setStartTime(slotStart);
                timeSlot.setEndTime(slotEnd);
                timeSlot.setType(BookingType.valueOf(type));
                timeSlot.setLimit(limit);

                dailySlots.add(timeSlot);
                slotsCounter++;
            }

            slotStart = slotEnd;

            if (breaks != null && breaks.getAfterSlots() > 0 && slotsCounter == breaks.getAfterSlots()) {
                slotStart = slotStart.plusMinutes(breaks.getBreakDurationMinutes());
                slotsCounter = 0;
            }
        }

        return dailySlots;
    }

    private boolean isOverlapping(LocalTime slotStart, LocalTime slotEnd, SchedulesConfig.TimeRange reserved) {
        return !(slotEnd.isBefore(reserved.getStart()) || slotStart.isAfter(reserved.getEnd()));
    }

    private List<DayOfWeek> parseWorkingDays(List<String> workingDays) {
        return workingDays.stream()
                .map(day -> DayOfWeek.valueOf(day.toUpperCase()))
                .collect(Collectors.toList());
    }
}