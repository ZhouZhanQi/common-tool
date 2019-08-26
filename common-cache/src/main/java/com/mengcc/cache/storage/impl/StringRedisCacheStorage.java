package com.mengcc.cache.storage.impl;

import com.mengcc.cache.storage.CacheStorage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 使用redis作为缓存, 存储string对象, 基于{@link StringRedisTemplate}进行操作。
 *
 * @author zhouzq
 * @date 2017-12-15
 */
public class StringRedisCacheStorage implements CacheStorage<String> {

    final private StringRedisTemplate stringRedisTemplate;
    final private String keyPrefix;

    public StringRedisCacheStorage(String keyPrefix, StringRedisTemplate stringRedisTemplate) {
        this.keyPrefix = keyPrefix;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public String getKeyPrefix() {
        return keyPrefix;
    }

    @Override
    public String get(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return stringRedisTemplate.opsForValue().get(prefix(key));
    }

    @Override
    public void delete(String key) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        stringRedisTemplate.delete(prefix(key));
    }

    @Override
    public boolean hasKey(String key) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        return stringRedisTemplate.hasKey(prefix(key));
    }

    @Override
    public Set<String> keys(String pattern, boolean prefix) {
        if (StringUtils.isBlank(pattern)) {
            return new HashSet<>();
        }
        String keyPattern = prefix ? prefix(pattern) : pattern;
        return stringRedisTemplate.keys(keyPattern);
    }

    @Override
    public void delete(Collection<String> keys) {
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
    }

    @Override
    public void deleteByPattern(String pattern) {
        if (StringUtils.isBlank(pattern)) {
            return;
        }
        Set<String> keys = stringRedisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            stringRedisTemplate.delete(keys);
        }
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
            stringRedisTemplate.delete(prefixKeys);
        }
    }

    @Override
    public void set(String key, String value) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        stringRedisTemplate.opsForValue().set(prefix(key), value);
    }

    @Override
    public void set(String key, String value, long timeout, TimeUnit unit) {
        if (StringUtils.isBlank(key)) {
            return;
        }
        stringRedisTemplate.opsForValue().set(prefix(key), value, timeout, unit);
    }

    @Override
    public boolean setIfAbsent(String key, String value) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        return stringRedisTemplate.opsForValue().setIfAbsent(prefix(key), value);
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        if (StringUtils.isBlank(key)) {
            return false;
        }
        return stringRedisTemplate.expire(prefix(key), timeout, unit);
    }

    @Override
    public Long increment(String key, int delta) {
        return stringRedisTemplate.opsForValue().increment(prefix(key), delta);
    }

    @Override
    public boolean isRedis() {
        return true;
    }

    @Override
    public boolean isStringRedis() {
        return true;
    }

    @Override
    public boolean isLocal() {
        return false;
    }

    private String prefix(String key) {
        return keyPrefix + key;
    }
}
