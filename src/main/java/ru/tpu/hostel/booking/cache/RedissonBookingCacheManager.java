package ru.tpu.hostel.booking.cache;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RLock;
import org.springframework.stereotype.Component;
import ru.tpu.hostel.booking.external.amqp.schedule.dto.Timeslot;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RedissonBookingCacheManager implements RedissonCacheManager<UUID, Timeslot> {

    private final RLocalCachedMap<UUID, Timeslot> bookingResponseCache;

    @Override
    public void putCacheAsync(UUID key, Timeslot timeslot) {
        bookingResponseCache.fastPutAsync(key, timeslot);
    }

    @Override
    public void putCacheAsync(List<Timeslot> timeslots) {
        Map<UUID, Timeslot> newTimeslots = timeslots.stream()
                .collect(Collectors.toMap(Timeslot::getId, Function.identity()));
        bookingResponseCache.putAllAsync(newTimeslots);
    }

    @Override
    public void putCache(UUID key, Timeslot value) {
        bookingResponseCache.fastPut(key, value);
    }

    @Override
    public Timeslot getCache(UUID key) {
        return bookingResponseCache.get(key);
    }

    @Override
    public void removeCache(UUID key) {
        bookingResponseCache.fastRemoveAsync(key);
    }

    @Override
    public void clear() {
        bookingResponseCache.clearAsync();
    }

    @Override
    public RLock getLock(UUID key) {
        return bookingResponseCache.getLock(key);
    }

}
