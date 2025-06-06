package ru.tpu.hostel.booking.cache;

import java.util.List;

public interface RedissonListCacheManager<K, V> {

    void putCache(K key, List<V> value);

    List<V> getCache(K key);

    void updateCache(K key, V element);

    void removeCache(K key, Object idOfElement);

    void clear();

}
