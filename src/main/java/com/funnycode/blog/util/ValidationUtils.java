package com.funnycode.blog.util;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.*;

/**
 * @author CC
 * @date 2019-12-13 16:53
 */
public class ValidationUtils {
    private static volatile Validator VALIDATOR;

    private ValidationUtils() {
    }

    /**
     * 获取或创建validator
     *
     * @return validator
     */
    @NonNull
    public static Validator getValidatorOrCreate() {
        if (VALIDATOR == null) {
            synchronized (ValidationUtils.class) {
                if(VALIDATOR == null){
                    //初始化validation
                    VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();
                }
            }
        }

        return VALIDATOR;
    }

    /**
     * 验证bean
     *
     * @param obj 被验证对象
     * @param groups 验证组
     * @throws ConstraintViolationException 验证失败抛出异常
     */
    public static void validate(Object obj, Class<?>... groups) {

        Validator validator = getValidatorOrCreate();

        //验证对象
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(obj, groups);
        if (!CollectionUtils.isEmpty(constraintViolations)) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }


    /**
     * 将字段验证错误转换为标准的map型
     *
     * @param constraintViolations 约束冲突
     * @return 错误信息 map
     */
    @NonNull
    public static Map<String, String> mapWithValidError(Set<ConstraintViolation<?>> constraintViolations) {
        if (CollectionUtils.isEmpty(constraintViolations)) {
            return Collections.emptyMap();
        }

        Map<String, String> errMap = new HashMap<>(4);
        //格式化错误消息
        constraintViolations.forEach(
                constraintViolation ->
                        errMap.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage()));
        return errMap;
    }

    /**
     * 将字段验证错误转换为标准的map型，key:value = field:message
     *
     * @param fieldErrors 字段错误组
     * @return 如果返回null，则表示未出现错误
     */
    public static Map<String, String> mapWithFieldError(@Nullable List<FieldError> fieldErrors) {
        if (CollectionUtils.isEmpty(fieldErrors)) {
            return Collections.emptyMap();
        }

        Map<String, String> errMap = new HashMap<>(4);
        fieldErrors.forEach(filedError -> errMap.put(filedError.getField(), filedError.getDefaultMessage()));
        return errMap;
    }
}
