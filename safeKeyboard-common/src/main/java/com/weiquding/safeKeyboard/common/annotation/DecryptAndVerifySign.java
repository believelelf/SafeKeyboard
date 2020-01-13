package com.weiquding.safeKeyboard.common.annotation;

import java.lang.annotation.*;

/**
 * 解密及验签数据
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/13
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface DecryptAndVerifySign {

}
