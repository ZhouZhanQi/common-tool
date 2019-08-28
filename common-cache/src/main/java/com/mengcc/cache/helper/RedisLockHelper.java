package com.mengcc.cache.helper;

import com.mengcc.cache.storage.CacheStorage;
import com.mengcc.core.context.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author zhouzq
 * @date 2019/8/13
 * @desc 缓存锁
 */
public class RedisLockHelper {

    private static final Logger log = LoggerFactory.getLogger(RedisLockHelper.class);

    /** 互斥锁的key */
    private static final String NXKEY = "nx";

    /** 使用互斥锁进行缓存获取时的最大重试次数 */
    private static final int MUTEX_MAX_RETRY = 5;

    /** 使用互斥锁进行缓存获取时的重试等待时长(毫秒) */
    private static final long MUTEX_RETRY_INTERVAL = 300L;

    /** 互斥锁自动失效的时间(秒) */
    private static final long MUTEX_EXPIRE_SEC = 120L;

    /** 互斥锁的默认值 */
    private static final String MUTEX_VALUE = "1";

    protected final CacheStorage storage;

    public RedisLockHelper(CacheStorage storage) {
        this.storage = storage;
    }

    /**
     * SETNX，是「SET if Not eXists」的缩写，也就是只有不存在的时候才设置，可以利用它来实现锁的效果。
     *
     * @param key 自动补上分布式锁的key的前缀
     * @return
     */
    public boolean lock(String key) {
        boolean flag = storage.setIfAbsent(NXKEY + key, MUTEX_VALUE);
        // 若获取成功，则设置一个失效时间，防止因为意外导致分布式锁一直锁定
        if (flag && MUTEX_EXPIRE_SEC > 0) {
            storage.expire(NXKEY + key, MUTEX_EXPIRE_SEC, TimeUnit.SECONDS);
        }

        if (flag) {
            return flag;
        }
        if (log.isDebugEnabled()) {
            log.debug(">> 获取缓存锁失败，尝试重新获取");
        }

        if (reachMaxRetryCount(key)) {
            log.warn(">> 获取缓存值时重试次数过多,将返回null, key: {}", key);
            return false;
        }

        try {
            //等待一段时间再获取
            Thread.sleep(MUTEX_RETRY_INTERVAL);
            //递归调用
            return lock(key);
        } catch (Exception e) {
            log.error(">> 缓存{}重试等待时出错", key, e);
        }

        return false;
    }

    public boolean unlock(String key) {
        Object lockValue = storage.get(key);
        //根本就没加过锁
        if (lockValue == null) {
            return true;
        }
        //不是同一个请求设的锁
        storage.delete(key);
        return true;
    }

    /** 在利用分布式锁获取缓存值时, 为了避免无限循环, 设定一个重试次数上限, 保存在线程上下文 */
    public boolean reachMaxRetryCount(String key) {
        String countKey = "cache-retry." + key;
        Integer count = ThreadContext.get(countKey, 1);
        if (log.isDebugEnabled()) {
            log.debug(">> 第{}次重试获取缓存: {}", count, key);
        }
        if (count > MUTEX_MAX_RETRY) {
            return true;
        } else {
            ThreadContext.set(countKey, count + 1);
            return false;
        }
    }
}
