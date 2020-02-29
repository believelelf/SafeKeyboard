package com.weiquding.safeKeyboard.common.core;

import com.weiquding.safeKeyboard.common.exception.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;

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
     * 错误信息消息束
     */
    private MessageSource errorMessageSource;
    /**
     * 默认错误码，用于无法解析错误码时使用
     */
    private String defaultMappingCode;

    public MessageSource getDefaultMessageSource() {
        return defaultMessageSource;
    }

    public MessageSource getErrorMessageSource() {
        return errorMessageSource;
    }

    public String getDefaultErrorKey() {
        return defaultMappingCode;
    }

    public void setDefaultMessageSource(MessageSource defaultMessageSource) {
        this.defaultMessageSource = defaultMessageSource;
    }

    public void setErrorMessageSource(MessageSource errorMessageSource) {
        this.errorMessageSource = errorMessageSource;
    }

    public void setDefaultMappingCode(String defaultMappingCode) {
        this.defaultMappingCode = defaultMappingCode;
    }

    @Override
    public Object resolveError(Throwable th, Locale locale) {
        // 检查是否包装异常
        if (ExceptionUtil.isWrapped(th)) {
            th = th.getCause();
        }
        // 检查特定异常：参数校验异常
        if (th instanceof MethodArgumentNotValidException) {
            return resolveMessageCode(errorMessageSource, ((MethodArgumentNotValidException) th).getBindingResult().getFieldError(), locale);
        }
        if (th instanceof ConstraintViolationException) {
            ConstraintViolationException cve = (ConstraintViolationException) th;
            Set<ConstraintViolation<?>> cvs = cve.getConstraintViolations();
            if (cvs != null && !cvs.isEmpty()) {
                ConstraintViolation<?> constraintViolation = cvs.iterator().next();
                return resolveMessageCode(errorMessageSource, constraintViolation.getPropertyPath().toString(), null, locale, defaultMappingCode, constraintViolation.getMessage());
            }
        }
        //  检查MessageSourceResolvable类型异常
        if (th instanceof MessageSourceResolvable) {
            MessageSourceResolvable msr = (MessageSourceResolvable) th;
            String[] codes = msr.getCodes();
            String code = codes != null && codes.length > 0 ? codes[0] : null;
            return resolveMessageCode(errorMessageSource, code, msr.getArguments(), locale, defaultMappingCode, msr.getDefaultMessage());
        }
        // 根据Throwable解析错误信息
        String errorMsg = null;
        String message = th.getMessage();
        if (StringUtils.hasText(message)) {
            errorMsg = resolveMessageCode(errorMessageSource, message, null, locale);
        }
        if (errorMsg != null) {
            return errorMsg;
        }
        // 使用异常全路径类名翻译错误信息
        message = th.getClass().getName();
        errorMsg = resolveMessageCode(errorMessageSource, message, null, locale);
        if (errorMsg != null) {
            return errorMsg;
        }
        // 使用默认错误码翻译错误信息
        message = this.defaultMappingCode;
        errorMsg = resolveMessageCode(errorMessageSource, message, null, locale);
        if (errorMsg != null) {
            return errorMsg;
        }
        return th.getClass().getName() + (th.getMessage() != null ? ":" + th.getMessage() : "");
    }

    @Override
    public Object resolveError(String errorCode, Object[] arguments, String defaultMessage, Locale locale) {
        String message = resolveMessageCode(errorMessageSource, errorCode, arguments, locale);
        return message == null ? defaultMessage : message;
    }

    @Override
    public String getMessage(String key, Locale locale) {
        return resolveMessageCode(defaultMessageSource, key, null, locale, null, key);
    }

    @Override
    public String getMessage(String key, Object[] arguments, Locale locale) {
        return resolveMessageCode(defaultMessageSource, key, arguments, locale, null, key);
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
