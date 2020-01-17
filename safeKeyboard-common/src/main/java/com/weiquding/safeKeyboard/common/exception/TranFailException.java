package com.weiquding.safeKeyboard.common.exception;

import java.io.Serializable;

/**
 * 交易异常
 *
 * @author believeyourself
 */
public class TranFailException extends BaseRuntimeException implements Serializable {


    private static final long serialVersionUID = 5191552605565024013L;


    public TranFailException(ErrorInfo errorInfo) {
        this(errorInfo, null, null);
    }

    public TranFailException(ErrorInfo errorInfo, Throwable th) {
        this(errorInfo, th, null);
    }

    public TranFailException(ErrorInfo errorInfo, String[] args) {
        this(errorInfo, null, args);
    }

    public TranFailException(ErrorInfo errorInfo, Object[] args) {
        this(errorInfo, null, args);
    }

    public TranFailException(ErrorInfo errorInfo, Throwable th, Object[] args) {
        super(errorInfo.getCode(), args, errorInfo.getDefaultMsg(), th);
    }

}