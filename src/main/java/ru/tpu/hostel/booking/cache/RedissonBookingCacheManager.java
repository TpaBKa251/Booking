package ru.tpu.hostel.booking.cache;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLocalCachedMap;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.tpu.hostel.booking.dto.response.BookingResponse;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RedissonBookingCacheManager implements RedissonListCacheManager<String, BookingResponse> {

    private final RLocalCachedMap<String, List<BookingResponse>> bookingResponseCache;

    @Override
    public void putCache(String key, List<BookingResponse> bookingResponses) {
        bookingResponseCache.fastPutAsync(key, bookingResponses.stream().toList());
    }

    @Override
    public List<BookingResponse> getCache(String key) {
        return bookingResponseCache.get(key);
    }

    @Async
    @Override
    public void updateCache(String key, BookingResponse bookingResponses) {
        List<BookingResponse> newCache = new ArrayList<>(bookingResponseCache.getOrDefault(key, new ArrayList<>()));
        newCache.add(bookingResponses);
        putCache(key, newCache);
    }

    @Async
    @Override
    public void removeCache(String key, Object id) {
        List<BookingResponse> cash = bookingResponseCache.getOrDefault(key, null);
        if (cash == null) {
            return;
        }
        List<BookingResponse> newCache = new ArrayList<>(cash);
        newCache.removeIf(b -> b.id().equals(id));
        putCache(key, newCache);
    }

    @Override
    public void clear() {
        bookingResponseCache.clearAsync();
    }

}
