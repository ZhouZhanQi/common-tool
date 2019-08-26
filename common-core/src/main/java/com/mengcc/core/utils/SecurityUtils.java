package com.mengcc.core.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author zhouzq
 * @date 2019/8/12
 * @desc 安全加密工具类
 */
public class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * 字符编码
     */
    private static final String CHARSET_NAME = "UTF-8";

    /**
     * MD5 加密字符串
     *
     * @param sourceStr
     * @return
     */
    public static String md5Encrypt(final String sourceStr) {
        return md5Encrypt(sourceStr, CHARSET_NAME);
    }

    /**
     * MD5加密字符串
     *
     * @param sourceStr 原始
     * @return 加密之后字符串
     */
    public static String md5Encrypt(final String sourceStr, String coding) {

        String md5Result = null;

        if (StringUtils.isBlank(sourceStr)) {
            return md5Result;
        }

        byte[] sourceByte;

        try {
            sourceByte = sourceStr.getBytes(coding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            sourceByte = sourceStr.getBytes();
        }

        // 用来将字节转换成 16 进制表示的字符
        final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(sourceByte);
            final byte tmp[] = md.digest(); // MD5 的计算结果是一个 128 位的长整数，
            // 用字节表示就是 16 个字节
            final char str[] = new char[16 * 2]; // 每个字节用 16 进制表示的话，使用两个字符，
            // 所以表示成 16 进制需要 32 个字符
            int k = 0; // 表示转换结果中对应的字符位置
            for (int i = 0; i < 16; i++) { // 从第一个字节开始，对 MD5 的每一个字节
                // 转换成 16 进制字符的转换
                final byte byte0 = tmp[i]; // 取第 i 个字节
                str[k++] = hexDigits[byte0 >>> 4 & 0xf]; // 取字节中高 4 位的数字转换,
                // >>>
                // 为逻辑右移，将符号位一起右移
                str[k++] = hexDigits[byte0 & 0xf]; // 取字节中低 4 位的数字转换
            }
            md5Result = new String(str); // 换后的结果转换为字符串
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return md5Result;
    }


}
