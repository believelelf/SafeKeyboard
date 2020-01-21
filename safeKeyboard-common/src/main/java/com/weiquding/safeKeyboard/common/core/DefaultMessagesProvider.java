package com.weiquding.safeKeyboard.common.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Locale;

/**
 * 默认消息束提供者
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/20
 */
@Slf4j
public class DefaultMessagesProvider implements MessagesProvider {

    /**
     * 默认消息束
     */
    private MessageSource defaultMessageSource;
    /**
     * 国际化文本消息束
     */
    private MessageSource valuesMessageSource;
    /**
     * 错误信息消息束
     */
    private MessageSource errorMessageSource;
    /**
     * 默认错误码，用于无法解析错误码时使用
     */
    private String defaultErrorKey;

    public MessageSource getDefaultMessageSource() {
        return defaultMessageSource;
    }

    public MessageSource getValuesMessageSource() {
        return valuesMessageSource;
    }

    public MessageSource getErrorMessageSource() {
        return errorMessageSource;
    }

    public String getDefaultErrorKey() {
        return defaultErrorKey;
    }

    @Override
    public Object resolveError(Throwable th, Locale locale) {
        return null;
    }

    @Override
    public Object resolveError(String errorCode, Object[] arguments, String defaultMessage, Locale locale) {
        return null;
    }

    @Override
    public String getMessage(String key, Locale locale) {
        return null;
    }

    @Override
    public String getMessage(String key, Object[] arguments, Locale locale) {
        return null;
    }


    /**
     * 解析消息文本
     *
     * @param messageSource  消息束
     * @param code           key
     * @param args           参数
     * @param locale         本地化语言
     * @param fallbackCode   降级key
     * @param defaultMessage 默认文本
     * @return 消息文件
     */
    private String resolveMessageCode(MessageSource messageSource, String code, Object[] args, Locale locale, String fallbackCode, String defaultMessage) {
        try {
            return messageSource.getMessage(code, args, locale);
        } catch (NoSuchMessageException e) {
            if (fallbackCode != null) {
                try {
                    return messageSource.getMessage(fallbackCode, args, locale);
                } catch (NoSuchMessageException ex) {
                    log.warn("No such Message: [{}] and [{}]", code, fallbackCode);
                }
            }
        }
        if (defaultMessage != null) {
            return defaultMessage;
        }
        if (args != null && args.length > 0) {
            return code + ", with args: " + Arrays.toString(args);
        }
        return code;
    }

    /**
     * 解析消息文本
     *
     * @param messageSource 消息束
     * @param code          key
     * @param args          参数
     * @param locale        本地化语言
     * @return 消息文件
     */
    private String resolveMessageCode(MessageSource messageSource, String code, Object[] args, Locale locale) {
        try {
            return messageSource.getMessage(code, args, locale);
        } catch (NoSuchMessageException e) {
            log.warn("No such Message: [{}]", code);
        }
        return null;
    }

    /**
     * 解析消息文本
     *
     * @param messageSource           消息束
     * @param messageSourceResolvable 消息束解析对象
     * @param locale                  本地化语言
     * @return 消息文件
     */
    @SuppressWarnings("all")
    private String resolveMessageCode(MessageSource messageSource, MessageSourceResolvable messageSourceResolvable, Locale locale) {
        Assert.notNull(messageSourceResolvable, "messageSourceResolvable must not be null");
        try {
            return messageSource.getMessage(messageSourceResolvable, locale);
        } catch (NoSuchMessageException e) {
            if (messageSourceResolvable.getDefaultMessage() != null) {
                return messageSourceResolvable.getDefaultMessage();
            }
        }
        Object[] args = messageSourceResolvable.getArguments();
        String[] codes = messageSourceResolvable.getCodes();
        Assert.notEmpty(codes, "The codes of messageSourceResolvable must not be empty");
        String code = codes[codes.length - 1];
        if (args != null && args.length > 0) {
            return code + ", with args: " + Arrays.toString(args);
        }
        return code;
    }
}
