package ru.tpu.hostel.booking.external.amqp.schedule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import ru.tpu.hostel.booking.cache.RedissonCacheManager;
import ru.tpu.hostel.booking.entity.Booking;
import ru.tpu.hostel.booking.external.amqp.schedule.dto.Timeslot;
import ru.tpu.hostel.booking.service.BookingService;
import ru.tpu.hostel.internal.utils.TimeUtil;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.tpu.hostel.booking.config.amqp.RabbitScheduleServiceConfiguration.SCHEDULES_SERVICE_LISTENER;

@Slf4j
@RequiredArgsConstructor
@Component
public class RabbitScheduleServiceListener {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS)
            .setTimeZone(TimeUtil.getTimeZone())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private final RedissonCacheManager<UUID, Timeslot> cacheManager;

    private final BookingService bookingService;

    @RabbitListener(
            queues = "${queueing.schedules-service.timeslot.queue-name}",
            containerFactory = SCHEDULES_SERVICE_LISTENER
    )
    public void receiveTimeslots(Message message) {
        cacheManager.clear();
        try {
            List<Timeslot> timeslots = MAPPER.readValue(message.getBody(), new TypeReference<>() {});
            cacheManager.putCacheAsync(timeslots);
            processBookingsInBatches(timeslots);
        } catch (Exception e) {
            log.error("Не удалось обновить кэш слотов", e);
        }
    }

    private void processBookingsInBatches(List<Timeslot> newTimeslots) {
        Map<UUID, Timeslot> newTimeslotMap = newTimeslots.stream()
                .collect(Collectors.toMap(Timeslot::getId, Function.identity()));

        Pageable pageable = Pageable.ofSize(200);
        Slice<Booking> bookingPage;
        do {
            bookingPage = bookingService.checkBookingsAfterUpdateCache(pageable, newTimeslotMap);
            pageable = pageable.next();
        } while (bookingPage.hasNext());
    }
}
