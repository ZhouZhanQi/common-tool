package com.mengcc.cache.storage.impl;

import com.mengcc.cache.config.RedisConfigHelper;
import com.mengcc.cache.storage.CacheStorage;
import com.mengcc.core.utils.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zhouzq
 * @date 2019/8/13
 * @desc 本地缓存
 */
public class LocalMemoryCacheStorage<V> implements CacheStorage<V> {

    private static final Logger log = LoggerFactory.getLogger(LocalMemoryCacheStorage.class);

    final private RedisSerializer<Object> serializer;

    final private ConcurrentHashMap<String, CacheValueWrapper> cache = new ConcurrentHashMap<>();

    final private String keyPrefix;

    /**
     * the self cleaning interval(in minutes)
     */
    private static final long CLEAN_INTERVAL = 30L;

    public LocalMemoryCacheStorage(String keyPrefix) {
        this.keyPrefix = keyPrefix;

        // 如果将对象直接放入内存, 程序中获取缓存后, 有可能进行修改, 导致缓存中的值也相应改变
        // 这里将缓存对象进行json序列化, 保存的是json字符串, 每次获取缓存再将json反序列化为对象, 保持缓存对象的不变性
        serializer = RedisConfigHelper.newJsonRedisSerializer();

        // 设置定时任务, 以便清理过期的缓存
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("cache-cleaning-thread-%d").daemon(true).build());
        executor.scheduleWithFixedDelay(cleaningTask(), CLEAN_INTERVAL, CLEAN_INTERVAL, TimeUnit.MINUTES);
    }

    @Override
    public boolean isRedis() {
        return false;
    }

    @Override
    public boolean isStringRedis() {
        return false;
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    @Override
    public String getKeyPrefix() {
        return this.keyPrefix;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(String key) {
        if (key == null) {
            return null;
        }
        CacheValueWrapper wrapper = cache.get(prefix(key));
        if (wrapper == null || wrapper.getCacheObject() == null) {
            return null;
        }
        if (wrapper.isExpired()) {
            cache.remove(prefix(key));
            return null;
        }
        return (V) serializer.deserialize(wrapper.getCacheObject());
    }

    @Override
    public void delete(String key) {
        if (key == null) {
            return;
        }
        cache.remove(prefix(key));
    }

    @Override
    public void deleteByPattern(String pattern) {
        if (StringUtils.isBlank(pattern)) {
            return;
        }
        Set<String> keys = keys(pattern, true);
        if (keys != null && !keys.isEmpty()) {
            delete(keys);
        }
    }

    @Override
    public boolean hasKey(String key) {
        return key != null && cache.containsKey(prefix(key));
    }

    @Override
    public Set<String> keys(String pattern, boolean prefix) {
        if (StringUtils.isBlank(pattern)) {
            return new HashSet<>();
        }
        String keyPattern = prefix ? prefix(pattern) : pattern;
        return cache.keySet().stream()
                .filter(Pattern.compile(keyPattern).asPredicate())
                .collect(Collectors.toSet());
    }

    @Override
    public void deleteWithPrex(Collection<String> keys) {
        if (keys != null && !keys.isEmpty()) {
            keys.forEach(key -> cache.remove(prefix(key)));
        }
    }

    @Override
    public void delete(Collection<String> keys) {
        if (keys != null && !keys.isEmpty()) {
            keys.forEach(cache::remove);
        }
    }

    @Override
    public void set(String key, V value) {
        if (key == null) {
            return;
        }
        byte[] wrapperValue = value == null ? null : serializer.serialize(value);
        CacheValueWrapper wrapper = new CacheValueWrapper(wrapperValue);
        cache.put(prefix(key), wrapper);
    }

    @Override
    public void set(String key, V value, long timeout, TimeUnit unit) {
        if (key == null) {
            return;
        }
        byte[] wrapperValue = value == null ? null : serializer.serialize(value);
        CacheValueWrapper wrapper;
        if (timeout > 0 && unit != null) {
            Long expire = unit.toSeconds(timeout);
            wrapper = new CacheValueWrapper(wrapperValue, expire.intValue());
        } else {
            wrapper = new CacheValueWrapper(wrapperValue);
        }
        cache.put(prefix(key), wrapper);
    }

    @Override
    public boolean setIfAbsent(String key, V value) {
        if (key == null) {
            return false;
        }
        byte[] wrapperValue = value == null ? null : serializer.serialize(value);
        CacheValueWrapper wrapper = new CacheValueWrapper(wrapperValue);
        return cache.putIfAbsent(prefix(key), wrapper) == null;
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        CacheValueWrapper wrapper = cache.get(prefix(key));
        if (wrapper == null || wrapper.isExpired()) {
            return false;
        }
        Long expire = unit.toSeconds(timeout);
        wrapper.setExpire(expire.intValue());
        return true;
    }

    @Override
    public Long increment(String key, int delta) {
        String prefixedKey = prefix(key);
        StringBuffer valueBuffer = new StringBuffer();
        CacheValueWrapper wrapper = cache.get(prefixedKey);
        if (wrapper == null) {
            valueBuffer.append(delta);
            byte[] wrapperValue = serializer.serialize(delta);
            // 若设置成功则返回null
            if (cache.putIfAbsent(prefixedKey, new CacheValueWrapper(wrapperValue)) != null) {
                try {
                    Thread.sleep(300L);
                    // 重试
                    increment(key, delta);
                } catch (InterruptedException e) {
                    log.error(">> 设置计数器失败: {}", e.getMessage(), e);
                }
            }
        } else {
            cache.computeIfPresent(prefixedKey, (theKey, oldWrapper) -> {
                Integer currentValue = (Integer)serializer.deserialize(oldWrapper.getCacheObject());
                int value = currentValue + delta;
                valueBuffer.append(value);
                byte[] newValue = serializer.serialize(value);
                return new CacheValueWrapper(newValue);
            });
        }
        return Long.parseLong(valueBuffer.toString());
    }

    private String prefix(String key) {
        return this.keyPrefix + key;
    }

    private Runnable cleaningTask() {
        return () -> {
            log.info(">> 开始执行内存缓存清理定时任务...");
            AtomicInteger count = new AtomicInteger(0);
            long total = cache.mappingCount();
            cache.keySet().stream().filter(key -> cache.get(key).isExpired())
                    .forEach(key -> {
                        // double check if it's expired or not
                        if (cache.get(key).isExpired()) {
                            cache.remove(key);
                            count.getAndIncrement();
                        }
                    });
            log.info(">> 内存缓存清理定时任务执行结束, 共有{}个缓存, 清理了{}个.", total, count.intValue());
        };
    }
}
