package com.weiquding.safeKeyboard.common.core;

import java.util.Locale;

/**
 * 国际化消息束支持
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/20
 */
public interface MessagesProvider {

    /**
     * 通过解析异常对象得到错误码及错误信息
     * @param th 异常
     * @param locale A  Locale object represents a specific geographical, political,or cultural region.
     * @return 错误信息
     */
    Object resolveError(Throwable th, Locale locale);

    /**
     * 通过解析错误码等得到错误码及错误信息
     * @param errorCode 错误码
     * @param arguments 错误参数
     * @param defaultMessage 默认错误信息
     * @param locale A  Locale object represents a specific geographical, political,or cultural region.
     * @return 错误信息
     */
    Object resolveError(String errorCode, Object[] arguments, String defaultMessage, Locale locale);

    /**
     * 通过属性名得到指定语言的国际化文本
     * @param key 属性名
     * @param locale A  Locale object represents a specific geographical, political,or cultural region.
     * @return 国际化文本
     */
    String getMessage(String key, Locale locale);


    /**
     * 通过属性名得到指定语言的国际化文本，支持参数替换
     *
     * @param key 属性名
     * @param arguments 参数
     * @param locale A  Locale object represents a specific geographical, political,or cultural region.
     * @return 国际化文本
     */
    String getMessage(String key, Object[] arguments, Locale locale);

}
