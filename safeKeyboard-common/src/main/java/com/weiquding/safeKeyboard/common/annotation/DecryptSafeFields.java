package com.weiquding.safeKeyboard.common.annotation;

import java.lang.annotation.*;

/**
 * 解密加密域，提供清理等机制
 *
 * @author wuby
 * @version V1.0
 * @date 2020/1/3
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DecryptSafeFields {


    /**
     * 允许的URI
     *
     * @return 允许的URI
     */
    String[] allowUris();

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


}
