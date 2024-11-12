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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@EnableScheduling
public class GymTimeSlotInitializer {

    private final TimeSlotRepository timeSlotRepository;

    @Bean
    public ApplicationRunner initializeGymTimeSlots() {
        return args -> addMissingGymSlots();
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Tomsk")
    public void generateGymTimeSlotsOnLastDay() {
        LocalDateTime startTime = getNextGymSlotStart(TimeNow.now().toLocalDate().plusDays(7).atTime(10, 0));

        for (int i = 0; i < 6; i++) {
            TimeSlot slot = new TimeSlot();
            slot.setStartTime(startTime);
            slot.setEndTime(startTime.plusMinutes(90));
            slot.setType(BookingType.GYM);
            slot.setLimit(11);
            startTime = startTime.plusHours(2);

            timeSlotRepository.save(slot);
        }
    }

    public void addMissingGymSlots() {
        LocalDateTime now = TimeNow.now();
        LocalDateTime endOfWeek = now.toLocalDate().plusDays(8).atTime(10, 0);

        TimeSlot lastSlot = timeSlotRepository.findLastByType(BookingType.GYM).orElse(null);
        LocalDateTime startTime = (lastSlot == null || lastSlot.getEndTime().isBefore(now))
                ? getNextGymSlotStart(now)
                : lastSlot.getEndTime();

        while (startTime.isBefore(endOfWeek)) {
            if (isGymSlotInRange(startTime)) {
                LocalDateTime endTime = startTime.plusMinutes(90);

                TimeSlot slot = new TimeSlot();
                slot.setStartTime(startTime);
                slot.setEndTime(endTime);
                slot.setType(BookingType.GYM);
                slot.setLimit(11);

                timeSlotRepository.save(slot);

                startTime = startTime.plusHours(2);
            } else {
                startTime = startTime.plusDays(1).with(LocalTime.of(10, 0));
            }
        }
    }

    private LocalDateTime getNextGymSlotStart(LocalDateTime current) {
        LocalTime baseStartTime = LocalTime.of(10, 0);
        LocalTime baseEndTime = LocalTime.of(21, 30);
        LocalDate date = current.toLocalDate();

        // Если текущее время раньше первого слота, возвращаем 10:00
        if (current.toLocalTime().isBefore(baseStartTime)) {
            return LocalDateTime.of(date, baseStartTime);
        }

        // Начинаем с округления вверх по времени к ближайшему допустимому слоту
        LocalTime roundedTime = current.toLocalTime().truncatedTo(ChronoUnit.HOURS);
        while (roundedTime.isBefore(baseEndTime) && ((roundedTime.getHour() - 10) % 2 != 0 || roundedTime.isBefore(current.toLocalTime()))) {
            roundedTime = roundedTime.plusHours(1);
        }

        // Если получили корректное время в рамках допустимого диапазона, возвращаем его
        if (!roundedTime.isAfter(baseEndTime)) {
            return LocalDateTime.of(date, roundedTime);
        }

        // Если текущее время после последнего слота дня, переходим к 10:00 следующего дня
        return LocalDateTime.of(date.plusDays(1), baseStartTime);
    }


    private boolean isGymSlotInRange(LocalDateTime time) {
        LocalTime localTime = time.toLocalTime();
        return !localTime.isBefore(LocalTime.of(10, 0)) && !localTime.isAfter(LocalTime.of(21, 0));
    }
}
