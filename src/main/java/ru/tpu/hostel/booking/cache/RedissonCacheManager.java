package ru.tpu.hostel.booking.cache;

import org.redisson.api.RLock;

import java.util.List;

public interface RedissonCacheManager<K, V> {

    void putCacheAsync(K key, V value);

    void putCacheAsync(List<V> value);

    void putCache(K key, V value);

    V getCache(K key);

    void removeCache(K key);

    void clear();

    RLock getLock(K key);

}
