package com.mengcc.core.utils;

import com.mengcc.core.exceptions.BaseUnCheckedException;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.Set;

/**
 * @author zhouzq
 * @date 2019/8/30
 * @desc 校验工具
 */
public class ValidatorUtils {


    /**
     * 校验默认规则
     * @param obj
     * @param <T>
     */
    public static <T> void  validate(T obj) {
        validate(obj, Default.class);
    }

    /**
     * 校验obj中的属性
     * @param obj
     * @param <T>
     * @param groups 分组
     */
    public static <T> void validate(T obj, Class<?>... groups) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(obj, groups);
        if (CollectionUtils.isEmpty(constraintViolations)) {
            return;
        }
        throw new ValidationException(convertErrorMsg(constraintViolations));
    }

    /**
     * 获取错误消息
     * @param violations
     * @param <T>
     * @return
     */
    private static <T> String convertErrorMsg(Set<ConstraintViolation<T>> violations) {
        ConstraintViolation<T> violation = violations.stream().findFirst().orElseThrow(() -> new BaseUnCheckedException("校验信息获取失败"));
        return violation.getMessage();
    }
}
