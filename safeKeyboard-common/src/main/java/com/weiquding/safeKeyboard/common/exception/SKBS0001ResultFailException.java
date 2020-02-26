package com.weiquding.safeKeyboard.common.exception;

import com.weiquding.safeKeyboard.common.format.ServiceType;

/**
 * 特定系统渠道异常：安全服务系统
 * 某些系统的错误码没有系统前缀，可以与其它系统错误码耦合，通过在原错误码之前补齐ServiceType以满足唯一性
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/26
 */
public class SKBS0001ResultFailException extends ResultFailException {

    public SKBS0001ResultFailException(String errorCode, String errorMsg, Object result, Object[] args) {
        super(ServiceType.SKBS0001 + errorCode, errorMsg, result, args);
    }

    public SKBS0001ResultFailException(String errorCode, String errorMsg, Object result) {
        super(ServiceType.SKBS0001 + errorCode, errorMsg, result);
    }

    public SKBS0001ResultFailException(ErrorInfo errorInfo, Object result, Object[] args) {
        this(ServiceType.SKBS0001 + errorInfo.getCode(), errorInfo.getDefaultMsg(), result, args);
    }

    public SKBS0001ResultFailException(ErrorInfo errorInfo, Object result) {
        this(errorInfo, result, null);
    }
}
