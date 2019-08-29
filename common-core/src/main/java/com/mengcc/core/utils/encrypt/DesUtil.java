package com.mengcc.core.utils.encrypt;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

/**
 * des加密
 *
 * @author ZhouZQ
 * @create 2019/3/21
 */
@Slf4j
public class DesUtil {

    /**
     * DES加密字符串
     *
     * @param sourceStr 原文字符串
     * @param keyStr    密钥字符串
     * @return
     */
    public static String desEncode(final String sourceStr, final String keyStr) {
        try {
            return Base64.getEncoder().encodeToString(desEncrypt(sourceStr.getBytes(StandardCharsets.UTF_8), keyStr.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.error(">> DES encrypt error", e);
        }
        return null;
    }


    /**
     * DES解密
     *
     * @param sourceStr 原文字符串
     * @param keyStr    密钥字符串
     * @return
     */
    public static Optional<String> desDecode(final String sourceStr, final String keyStr) {
        try {
            byte[] result = Base64.getDecoder().decode(sourceStr);
            String decodeStr =  new String(desDecrypt(result, keyStr.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
            return Optional.of(decodeStr);
        } catch (Exception e) {
            log.error(">> DES decrypt error", e);
            return Optional.empty();
        }
    }

    private static byte[] desEncrypt(byte[] plainText, byte[] rawKeyData) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(rawKeyData);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DesUtils");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(1, key, sr);
        return cipher.doFinal(plainText);
    }

    private static byte[] desDecrypt(byte[] encryptText, byte[] rawKeyData) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(rawKeyData);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DesUtils");
        SecretKey key = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(2, key, sr);
        return cipher.doFinal(encryptText);
    }
}
