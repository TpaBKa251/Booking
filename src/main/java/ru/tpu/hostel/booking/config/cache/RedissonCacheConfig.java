package ru.tpu.hostel.booking.config.cache;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.LocalCachedMapOptions;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import ru.tpu.hostel.booking.dto.response.BookingResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RedissonCacheConfig {

    public static final String KEY_PATTERN = "%s_%s";

    @Value("classpath:redisson.yaml")
    private Resource cacheFile;

    @Bean
    public RedissonClient redisson() throws IOException {
        Config config = Config.fromYAML(cacheFile.getInputStream());
        return Redisson.create(config);
    }

    @Bean
    public RLocalCachedMap<String, List<BookingResponse>> createBookingMapCache(RedissonClient redisson) {
        LocalCachedMapOptions<String, List<BookingResponse>> options = LocalCachedMapOptions.name("booking");

        options.cacheSize(10_000)
                .timeout(Duration.ofSeconds(2))
                .evictionPolicy(LocalCachedMapOptions.EvictionPolicy.LRU)
                .timeToLive(Duration.ofMinutes(10))
                .maxIdle(Duration.ofMinutes(5))
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.NONE)
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .cacheProvider(LocalCachedMapOptions.CacheProvider.CAFFEINE);

        return redisson.getLocalCachedMap(options);
    }

}