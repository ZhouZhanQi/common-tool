package com.mengcc.cache.storage;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存操作接口
 *
 * @author zhouzq
 * @date 2017-12-14
 */
public interface CacheStorage<V> {

    /**
     * 是否使用StringRedisTemplate或RedisTemplate作为缓存
     * @return
     */
    boolean isRedis();

    /**
     * 是否使用StringRedisTemplate作为缓存
     * @return
     */
    boolean isStringRedis();

    /**
     * 是否使用内存作为缓存
     * @return
     */
    boolean isLocal();

    /**
     * key的统一前缀
     * @return
     */
    String getKeyPrefix();

    /**
     * 获取key对应的缓存值
     * @param key
     * @return key对应的缓存值
     */
    V get(String key);

    /**
     * 删除指定key的缓存
     * @param key
     */
    void delete(String key);

    /**
     * 根据正则表达匹配批量删除key
     * @param pattern 无需前缀
     */
    void deleteByPattern(String pattern);

    /**
     * 判断指定的key是否存在
     * @param key
     * @return 若缓存中有此key, 则返回{@code true}
     */
    boolean hasKey(String key);

    /**
     * 根据正则表达模版, 获取符合的key列表
     * @param pattern
     * @param prefix 是否为正则加上key前缀
     * @return 匹配的key集合
     */
    Set<String> keys(String pattern, boolean prefix);

    /**
     * 批量删除集合里指定的缓存
     * @param keys 无需前缀,方法执行时自动为每个key加上前缀
     */
    void deleteWithPrex(Collection<String> keys);

    /**
     * 批量删除缓存
     * @param keys 每个key都必须带有前缀
     */
    void delete(Collection<String> keys);

    /**
     * 设置key对应的缓存值
     * @param key
     * @param value
     */
    void set(String key, V value);

    /**
     * 设置key对应的缓存, 并指定失效时间
     * @param key
     * @param value
     * @param timeout
     * @param unit
     */
    void set(String key, V value, long timeout, TimeUnit unit);

    /**
     * 仅当key不存在时, 才设置key对应的缓存值
     * @param key
     * @param value
     * @return 设置成功则返回{@code true}
     */
    boolean setIfAbsent(String key, V value);

    /**
     * 为给定key设置存活时间
     * @param key
     * @param timeout
     * @param unit
     * @return 成功设置则返回{@code true}
     */
    boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * 增长一个整数值, 用于计数器
     * @param key
     * @param delta
     * @return
     */
    Long increment(String key, int delta);
}
