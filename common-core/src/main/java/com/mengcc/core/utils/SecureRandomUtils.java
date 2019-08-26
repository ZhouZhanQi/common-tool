package com.mengcc.core.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 随机对象帮助类
 *
 * @author zhouzq
 * @date 2018-05-28
 */
public final class SecureRandomUtils {

    private SecureRandomUtils() {
    }


    /**
     * Get strong enough SecureRandom instance and of the checked exception.
     * TODO Try {@code NativePRNGNonBlocking} and failover to default SHA1PRNG until Java 9.
     *
     * @return the strong instance
     */
    public static SecureRandom getNativeInstance() {
        try {
            return SecureRandom.getInstance("NativePRNGNonBlocking");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return new SecureRandom();
    }
}
