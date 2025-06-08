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
import ru.tpu.hostel.booking.external.amqp.schedule.dto.Timeslot;

import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({RedissonLocalCacheProperties.class, RedissonClientProperties.class})
public class RedissonCacheConfig {

    public static final String KEY_PATTERN = "%s_%s";

    @Bean
    public RedissonClient redisson(RedissonClientProperties properties) {
        Config config = new Config();

        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress(properties.getAddress())
                .setPassword(properties.getPassword())
                .setDatabase(properties.getDatabase())
                .setTimeout(properties.getTimeout())
                .setConnectTimeout(properties.getConnectionTimeout())
                .setRetryAttempts(properties.getRetryAttempts())
                .setConnectionPoolSize(properties.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(properties.getConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(properties.getSubscriptionConnectionPoolSize())
                .setSubscriptionConnectionMinimumIdleSize(properties.getSubscriptionConnectionMinimumIdleSize())
                .setIdleConnectionTimeout(properties.getIdleConnectionTimeout())
                .setPingConnectionInterval(properties.getPingConnectionInterval())
                .setKeepAlive(properties.getKeepAlive())
                .setTcpNoDelay(properties.getTcpNoDelay())
                .setClientName(properties.getClientName());

        return Redisson.create(config);
    }

    @Bean
    public RLocalCachedMap<UUID, Timeslot> createBookingMapCache(
            RedissonClient redisson,
            RedissonLocalCacheProperties cacheProperties
    ) {
        LocalCachedMapOptions<UUID, Timeslot> options = LocalCachedMapOptions
                .<UUID, Timeslot>name(cacheProperties.cacheName())
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