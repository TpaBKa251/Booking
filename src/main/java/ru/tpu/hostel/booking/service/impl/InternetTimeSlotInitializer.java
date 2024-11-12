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
import ru.tpu.hostel.booking.utils.TimeNow;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class InternetTimeSlotInitializer {

    private final TimeSlotRepository timeSlotRepository;

    @Bean
    public ApplicationRunner initializeTimeSlots() {
        return args -> addMissingSlots();
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Tomsk")
    public void generateTimeSlotsOnLastDay() {
        LocalDateTime startTime = TimeNow.now()
                .withSecond(0)
                .withNano(0)
                .plusDays(7)
                .plusHours(20);

        for (int i = 0; i < 24; i++) {
            TimeSlot slot = new TimeSlot();
            slot.setStartTime(startTime);
            slot.setEndTime(startTime.plusMinutes(10));
            slot.setType(BookingType.INTERNET);
            slot.setLimit(1);
            startTime = startTime.plusMinutes(10);

            timeSlotRepository.save(slot);
        }
    }

    public void addMissingSlots() {
        LocalDateTime now = TimeNow.now();
        LocalDateTime endOfWeek = now.toLocalDate().plusDays(8).atStartOfDay().plusHours(20);

        TimeSlot lastSlot = timeSlotRepository.findLastByType(BookingType.INTERNET).orElse(null);

        LocalDateTime startTime = (lastSlot == null || lastSlot.getEndTime().isBefore(now)) ? now : lastSlot.getEndTime();

        if (startTime.isBefore(now)) {
            startTime = getNextAvailableTimeSlot(now);
        }

        while (startTime.isBefore(endOfWeek)) {
            if (isTimeInSlotRange(startTime)) {
                LocalDateTime endTime = startTime.plusMinutes(10);

                TimeSlot slot = new TimeSlot();
                slot.setStartTime(startTime);
                slot.setEndTime(endTime);
                slot.setType(BookingType.INTERNET);
                slot.setLimit(1);

                timeSlotRepository.save(slot);

                startTime = endTime;
            } else {
                if (
                        startTime.toLocalTime().equals(LocalTime.MIDNIGHT)
                                || startTime.toLocalTime().isBefore(LocalTime.of(20, 0))
                ) {
                    startTime = startTime.with(LocalTime.of(20, 0));
                } else {
                    startTime = startTime.plusDays(1).with(LocalTime.of(20, 0));
                }
            }
        }
    }

    private LocalDateTime getNextAvailableTimeSlot(LocalDateTime now) {
        LocalTime nextSlotTime = now.toLocalTime().truncatedTo(ChronoUnit.MINUTES).plusMinutes(10 - (now.getMinute() % 10));

        if (nextSlotTime.isBefore(LocalTime.of(20, 0))) {
            return now.toLocalDate().atTime(20, 0);
        } else if (nextSlotTime.isAfter(LocalTime.of(23, 50))) {
            return now.toLocalDate().plusDays(1).atTime(20, 0);
        } else {
            return now.toLocalDate().atTime(nextSlotTime);
        }
    }

    private boolean isTimeInSlotRange(LocalDateTime time) {
        LocalTime localTime = time.toLocalTime();

        return !localTime.isBefore(LocalTime.of(20, 0)) && !localTime.isAfter(LocalTime.of(23, 50));
    }
}
