package com.weiquding.safeKeyboard.common.annotation;

import java.lang.annotation.*;

/**
 * 设置加密安全域
 *
 * @author wuby
 * @version V1.0
 * @date 2020/1/3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface EncryptSafeFields {

    /**
     * 待加密字段
     * @return 字段
     */
    String[] fields();

    /**
     * 加密域字段名
     *
     * @return 字段名
     */
    String name() default "safeFields";

    /**
     * 安全提供者
     *
     * @return 提供者名称
     */
    String safeProvider() default "safeProvider";

    /**
     * 是否清理原字段
     *
     * @return 清理与否
     */
    boolean clean() default false;

}
