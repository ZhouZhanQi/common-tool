package com.mengcc.cache.storage.impl;

import com.mengcc.cache.storage.CacheStorage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 使用redis作为缓存, 存储pojo对象, 基于{@link RedisTemplate}进行操作。
 *
 * @author zhouzq
 * @date 2017-12-15
 */
public class RedisCacheStorage implements CacheStorage<Object> {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheStorage.class);

    final private RedisTemplate<String, Object> redisTemplate;
    final private String keyPrefix;

    public RedisCacheStorage(String keyPrefix, RedisTemplate<String, Object> redisTemplate) {
        this.keyPrefix = keyPrefix;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public String getKeyPrefix() {
        return keyPrefix;
    }

    @Override
    public Object get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return redisTemplate.boundValueOps(prefix(key)).get();
    }

    @Override
    public void delete(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        redisTemplate.delete(prefix(key));
    }

    @Override
    public void deleteByPattern(String pattern) {
        if (StringUtils.isBlank(pattern)) {
            return;
        }
        Set<String> keys = redisTemplate.keys(prefix(pattern));
        if (keys != null && !keys.isEmpty()) {
            log.debug(">> [deleteByPattern] deleting keys: {}", keys);
            redisTemplate.delete(keys);
        }
    }

    @Override
    public boolean hasKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }

        return redisTemplate.hasKey(prefix(key));
    }

    @Override
    public Set<String> keys(String pattern, boolean prefix) {
        if (StringUtils.isBlank(pattern)) {
            return new HashSet<>();
        }
        String keyPattern = prefix ? prefix(pattern) : pattern;
        return redisTemplate.keys(keyPattern);
    }

    @Override
    public void deleteWithPrex(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        Collection<String> prefixKeys = keys.stream()
                .filter(StringUtils::isNotBlank)
                .map(this::prefix)
                .distinct()
                .collect(Collectors.toList());
        if (!prefixKeys.isEmpty()) {
            log.debug(">> [deleteWithPrex] deleting keys: {}", prefixKeys);
            redisTemplate.delete(prefixKeys);
        }
    }

    @Override
    public void delete(Collection<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        log.debug(">> [delete(Collection)] deleting keys: {}", keys);
        redisTemplate.delete(keys);
    }

    @Override
    public void set(String key, Object value) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        redisTemplate.opsForValue().set(prefix(key), value);
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        redisTemplate.opsForValue().set(prefix(key), value, timeout, unit);
    }

    @Override
    public boolean setIfAbsent(String key, Object value) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        return redisTemplate.opsForValue().setIfAbsent(prefix(key), value);
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        return redisTemplate.expire(prefix(key), timeout, unit);
    }

    @Override
    public Long increment(String key, int delta) {
        return redisTemplate.opsForValue().increment(prefix(key), delta);
    }

    @Override
    public boolean isRedis() {
        return true;
    }

    @Override
    public boolean isStringRedis() {
        return false;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    private String prefix(String key) {
        return keyPrefix + key;
    }
}
