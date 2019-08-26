package com.mengcc.cache.storage.impl;

/**
 * 缓存值的封装类。
 * <p>有些缓存不允许保存空值(比如: ConcurrentHashMap), 将值封装后可以保证空值也可以保存</p>
 *
 * @author zhouzq
 * @date 2017-12-14
 */
class CacheValueWrapper {
    /** 缓存数据 */
    private byte[] cacheObject;

    /** 最后加载时间 */
    private long lastLoadTime;

    /** 缓存时长(秒) */
    private int expire;

    CacheValueWrapper(byte[] cacheObject) {
        this(cacheObject, -1);
    }

    CacheValueWrapper(byte[] cacheObject, int expire) {
        this.cacheObject = cacheObject;
        this.lastLoadTime = System.currentTimeMillis();
        this.expire = expire;
    }

    boolean isExpired() {
        return expire > 0 && ((System.currentTimeMillis() - lastLoadTime) > (expire * 1000));
    }

    byte[] getCacheObject() {
        lastLoadTime = System.currentTimeMillis();
        return cacheObject;
    }

    void setExpire(int expire) {
        lastLoadTime = System.currentTimeMillis();
        this.expire = expire;
    }
}
