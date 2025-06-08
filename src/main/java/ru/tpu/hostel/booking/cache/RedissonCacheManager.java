package ru.tpu.hostel.booking.cache;

import org.redisson.api.RLock;

import java.util.List;

public interface RedissonCacheManager<K, V> {

    void putCache(K key, V value);

    void putCache(List<V> value);

    V getCache(K key);

    void removeCache(K key);

    void clear();

    RLock getLock(K key);

}
