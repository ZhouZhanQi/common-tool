package com.mengcc.core.helper;

import java.util.Objects;

/**
 * number类型转换帮助类
 *
 * @author ZhouZQ
 * @create 2019/3/22
 */
public class NumberConvertHelper {

    /**
     * This class can't be instantiated, exposes static utility methods only.
     */
    private NumberConvertHelper() {
    }

    /**
     * converts Number to Byte
     *
     * @param number
     * @return
     */
    public static Byte toByte(Number number) {
        if (Objects.isNull(number)) {
            return null;
        }

        if (number instanceof Byte) {
            // Avoids unnecessary unbox/box
            return (Byte) number;
        }

        return number.byteValue();
    }

    /**
     * converts Number to Byte
     *
     * @param number
     * @return
     */
    public static Short toShort(Number number) {
        if (Objects.isNull(number)) {
           return null;
        }

        if (number instanceof Short) {
            // Avoids unnecessary unbox/box
            return (Short) number;
        }

        return number.shortValue();
    }

    /**
     * converts Number to Integer
     *
     * @param number
     * @return
     */
    public static Integer toInteger(Number number) {
        if (Objects.isNull(number)) {
            return null;
        }
        if (number instanceof Integer) {
            // Avoids unnecessary unbox/box
            return (Integer) number;
        }

        return number.intValue();
    }

    /**
     * converts Number to Long
     *
     * @param number
     * @return
     */
    public static Long toLong(Number number) {
        if (Objects.isNull(number)) {
            return null;
        }
        if (number instanceof Long) {
            // Avoids unnecessary unbox/box
            return (Long) number;
        }

        return number.longValue();
    }

    /**
     * converts Number to Double
     *
     * @param number
     * @return
     */
    public static Double toDouble(Number number) {
        if (Objects.isNull(number)) {
            return null;
        }
        if (number instanceof Double) {
            // Avoids unnecessary unbox/box
            return (Double) number;
        }

        return number.doubleValue();
    }

    /**
     * converts Number to Float
     *
     * @param number
     * @return
     */
    public static Float toFloat(Number number) {
        if (Objects.isNull(number)) {
            return null;
        }

        if (number instanceof Float) {
            // Avoids unnecessary unbox/box
            return (Float) number;
        }

        return number.floatValue();
    }
}
