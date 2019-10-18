package com.mengcc.cache.aspect;

import com.mengcc.cache.storage.CacheStorage;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理基于注解的缓存的切面AOP
 *
 * @author zhouzq
 * @date 2018-01-08
 */
@Aspect
public class CacheHandleAspect {

    private static final Logger log = LoggerFactory.getLogger(CacheHandleAspect.class);

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

    public CacheHandleAspect(CacheStorage cacheStorage) {
        this.storage = cacheStorage;
    }


    private boolean isValidKeyParamType(Object param) {
        return (param instanceof String) || (param instanceof Long) || (param instanceof Integer) || (param instanceof Short)
                || (param instanceof Byte);
    }
}
