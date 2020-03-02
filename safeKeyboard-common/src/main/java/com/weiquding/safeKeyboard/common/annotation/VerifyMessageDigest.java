package com.weiquding.safeKeyboard.common.annotation;

import java.lang.annotation.*;

/**
 * 验证消息摘要
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/3/1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface VerifyMessageDigest {

    /**
     * 是否需要对参数进行URL
     * @return 默认为false
     */
    boolean needUrlEncoding() default false;
}
