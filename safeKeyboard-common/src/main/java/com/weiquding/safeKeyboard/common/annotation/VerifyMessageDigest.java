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
}
