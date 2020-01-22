package com.weiquding.safeKeyboard.common.exception;

import java.util.Locale;

/**
 * 异常处理器
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/22
 */
public interface ExceptionHandler {

    /**
     * 处理异常，转换错误信息
     *
     * @param throwable 异常
     * @param locale    本地化语言
     * @return 错误信息
     */
    ErrorDetail handleException(Throwable throwable, Locale locale);

}
