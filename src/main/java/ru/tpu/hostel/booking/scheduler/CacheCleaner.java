package ru.tpu.hostel.booking.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.booking.cache.RedissonListCacheManager;
import ru.tpu.hostel.booking.dto.response.BookingResponse;

@Service
@RequiredArgsConstructor
public class CacheCleaner {

    private final RedissonListCacheManager<String, BookingResponse> cacheManager;

    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Tomsk")
    public void cleanCache() {
        cacheManager.clear();
    }
}
