package com.weiquding.safeKeyboard.common.exception;

import com.weiquding.safeKeyboard.common.core.MessagesProvider;
import com.weiquding.safeKeyboard.common.core.SystemConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.util.Assert;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 默认错误信息处理器
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/22
 */
@Slf4j
public class DefaultExceptionHandler implements ExceptionHandler, InitializingBean {

    private Pattern regex;
    private MessagesProvider messagesProvider;
    private Map<String, String> messageCodeMapping;
    private String defaultMappingCode;
    private boolean rebuildMessageCode;
    private boolean outputHostName;
    private SystemConfig systemConfig;

    public DefaultExceptionHandler() {
        this.regex = Pattern.compile("BASE[A-Z]{2}[0-9]{4}");
        this.rebuildMessageCode = true;
    }

    @Override
    public ErrorDetail handleException(Throwable throwable, Locale locale) {
        String errorCode = null;
        String errorMsg = null;
        String cause = null;
        ErrorDetail.Severity severity = null;
        if (throwable == null) {
            return new ErrorDetail();
        }
        try {
            throwable = unwrapThrowable(throwable);
            cause = throwable.getMessage();
            severity = throwable instanceof Exception ? ErrorDetail.Severity.EXCEPTION : ErrorDetail.Severity.ERROR;

            // 翻译错误码及错误信息
            String defaultMessage = null;
            Object[] arguments = null;
            String className = throwable.getClass().getName();
            errorCode = (String) messagesProvider.resolveError(className, null, null, locale);
            if (errorCode == null || errorCode.equals(className)) {
                errorCode = null;
                if ((throwable instanceof MessageSourceResolvable)) {
                    MessageSourceResolvable messagesourceresolvable = (MessageSourceResolvable) throwable;
                    String[] codes = messagesourceresolvable.getCodes();
                    if (codes != null && codes.length > 0) {
                        errorCode = codes[0];
                    }
                    if (messagesourceresolvable.getDefaultMessage() != null) {
                        defaultMessage = messagesourceresolvable.getDefaultMessage();
                    }
                    arguments = messagesourceresolvable.getArguments();
                }
            }
            if (errorCode == null) {
                errorCode = BaseBPError.UNKNOWN.getInfo().getCode();
                errorMsg = (String) messagesProvider.resolveError(throwable, locale);
            } else {
                errorCode = mappingCode(errorCode);
                errorMsg = (String) messagesProvider.resolveError(errorCode, arguments, defaultMessage, locale);
            }
        } catch (Exception e) {
            log.warn("An error occurred while parsing the exception information", e);
        }
        return getErrorDetail(errorCode, errorMsg, cause, severity);
    }

    private String mappingCode(String errorCode) {
        if (messageCodeMapping != null) {
            String mappingCode = messageCodeMapping.get(errorCode);
            if (mappingCode != null) {
                errorCode = mappingCode;
            } else if (defaultMappingCode != null) {
                errorCode = defaultMappingCode;
            }
        }
        return errorCode;
    }

    private ErrorDetail getErrorDetail(String errorCode, String errorMsg, String cause, ErrorDetail.Severity severity) {
        // 转换错误码
        if (systemConfig != null && this.rebuildMessageCode) {
            errorCode = compile(errorCode, systemConfig.getSystemCode());
        }
        ErrorDetail errorDetail = new ErrorDetail(errorCode, errorMsg, cause, severity);
        if ((this.systemConfig != null) && this.outputHostName) {
            String hostName = this.systemConfig.getHostName();
            errorDetail.setHostName(hostName);
        }
        return errorDetail;
    }

    private Throwable unwrapThrowable(Throwable throwable) {
        // unwrap exception
        while (ExceptionUtil.isWrapped(throwable)) {
            Throwable causeThrowable = throwable.getCause();
            if (causeThrowable == null || causeThrowable == throwable) {
                break;
            }
            throwable = causeThrowable;
        }
        return throwable;
    }

    protected String compile(String errorCode, String systemCode) {
        try {
            if (errorCode != null) {
                if (regex.matcher(errorCode).matches()) {
                    return errorCode.replace("BASE", systemCode);
                }
                return errorCode;
            }
        } catch (Exception e) {
            log.warn("An error occurred while parsing the error code", e);
        }
        return defaultMappingCode;
    }

    public void setRegex(String regex) {
        this.regex = Pattern.compile(regex);
    }

    public void setMessagesProvider(MessagesProvider messagesProvider) {
        this.messagesProvider = messagesProvider;
    }

    public void setMessageCodeMapping(Map<String, String> messageCodeMapping) {
        this.messageCodeMapping = messageCodeMapping;
    }

    public void setDefaultMappingCode(String defaultMappingCode) {
        this.defaultMappingCode = defaultMappingCode;
    }

    public void setRebuildMessageCode(boolean rebuildMessageCode) {
        this.rebuildMessageCode = rebuildMessageCode;
    }

    public void setOutputHostName(boolean outputHostName) {
        this.outputHostName = outputHostName;
    }

    public void setSystemConfig(SystemConfig systemConfig) {
        this.systemConfig = systemConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(defaultMappingCode, "defaultMappingCode must not be null");
    }
}
