package ru.tpu.hostel.booking.config.cache;

import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.LocalCachedMapOptions;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tpu.hostel.booking.dto.response.BookingResponse;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({RedissonLocalCacheProperties.class, RedissonClientProperties.class})
public class RedissonCacheConfig {

    public static final String KEY_PATTERN = "%s_%s";

    @Bean
    public RedissonClient redisson(RedissonClientProperties properties) {
        Config config = new Config();

        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress(properties.address())
                .setPassword(properties.password())
                .setDatabase(properties.database())
                .setTimeout(5000)
                .setConnectTimeout(10000)
                .setRetryAttempts(2)
                .setConnectionPoolSize(30)
                .setConnectionMinimumIdleSize(10)
                .setSubscriptionConnectionPoolSize(10)
                .setSubscriptionConnectionMinimumIdleSize(1)
                .setIdleConnectionTimeout(10000)
                .setPingConnectionInterval(30000)
                .setKeepAlive(true)
                .setTcpNoDelay(true)
                .setClientName("bookng-redisson");

        return Redisson.create(config);
    }

    @Bean
    public RLocalCachedMap<String, List<BookingResponse>> createBookingMapCache(
            RedissonClient redisson,
            RedissonLocalCacheProperties cacheProperties
    ) {
        LocalCachedMapOptions<String, List<BookingResponse>> options = LocalCachedMapOptions
                .<String, List<BookingResponse>>name(cacheProperties.cacheName())
                .cacheSize(cacheProperties.cashSize())
                .timeout(cacheProperties.timeout())
                .evictionPolicy(LocalCachedMapOptions.EvictionPolicy.LRU)
                .timeToLive(cacheProperties.ttl())
                .maxIdle(cacheProperties.maxIdle())
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.NONE)
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .cacheProvider(LocalCachedMapOptions.CacheProvider.CAFFEINE);

        return redisson.getLocalCachedMap(options);
    }

}