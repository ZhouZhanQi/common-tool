package com.mengcc.core.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author zhouzq
 * @date 2019/5/31
 * @desc 随机字符串工具类
 */
public final class RandomStrUtils {

    private static final String ALPHABETIC_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String NUMERIC_CHARS = "0123456789";

    private static final String DEFAULT_CHARS = ALPHABETIC_CHARS + NUMERIC_CHARS;


    private RandomStrUtils() {
    }

    /** An instance of secure random to ensure randomness is secure. */
    private static final SecureRandom RANDOMIZER = SecureRandomUtils.getNativeInstance();

    /**
     * 生成指定长度的随机字符串,仅包含数字
     *
     * @param length 随机字符串长度
     * @return
     */
    public static String randomNumeric(int length) {
        return random(length, NUMERIC_CHARS);
    }

    /**
     * 生成指定长度的随机字符串,可能包含小写字母和大写字母
     *
     * @param length 随机字符串长度
     * @return
     */
    public static String randomAlphabetic(int length) {
        return random(length, ALPHABETIC_CHARS);
    }

    /**
     * 生成指定长度的随机字符串,可能包含小写字母+大写字母+数字
     *
     * @param length 随机字符串长度
     * @return
     */
    public static String randomAlphanumeric(int length) {
        return random(length, DEFAULT_CHARS);
    }

    /**
     * 根据指定的字符全集,生成指定长度的随机字符串
     *
     * @param length 随机字符串长度
     * @param chars  随机字符全集
     * @return
     */
    public static String random(int length, char[] chars) {
        if (chars == null || chars.length == 0) {
            throw new IllegalArgumentException("unspecified character set creat random string error");
        }
        return random(length, new String(chars));
    }

    /**
     * 根据指定的字符全集,生成指定长度的随机字符串
     *
     * @param length 随机字符串长度
     * @param chars  随机字符全集
     * @return
     */
    public static String random(int length, String chars) {
        if (length <= 0) {
            throw new IllegalArgumentException("random string length is error: " + length);
        }

        if (StringUtils.isBlank(chars)) {
            throw new IllegalArgumentException("unspecified character set creat random string error");
        }

        return IntStream.range(0, length)
                .map(i -> RANDOMIZER.nextInt(chars.length()))
                .mapToObj(randomInt -> chars.substring(randomInt, randomInt + 1))
                .collect(Collectors.joining());
    }

    /**
     * 生成一个指定长度的的随机字节数组,并转换为16进制的字符串
     * @param size 字节数组长度
     * @return
     */
    public static String randomHexString(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("random string length is error: " + size);
        }
        try {
            final char[] hexChars = Hex.encodeHex(randomStringBytes(size));
            return String.valueOf(hexChars);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成一个指定长度的的随机字节数组,并转换为base64 url encode的字符串
     * @param size 字节数组长度
     * @return
     */
    public static String randomBase64UrlSafedString(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("random string length is error: " + size);
        }

        return Base64.encodeBase64URLSafeString(randomStringBytes(size));
    }

    private static byte[] randomStringBytes(int size) {
        final byte[] randomBytes = new byte[size];
        RANDOMIZER.nextBytes(randomBytes);
        return randomBytes;
    }

}
