package com.weiquding.safeKeyboard.common.exception;

import org.springframework.context.MessageSourceResolvable;

import java.io.Serializable;

/**
 * 交易异常
 *
 * @author believeyourself
 */
public class TranFailException extends RuntimeException implements MessageSourceResolvable, Serializable {


    private static final long serialVersionUID = 5191552605565024013L;

    /**
     * 交易参数
     */
    private Object[] args;
    /**
     * 错误码
     */
    private String[] codes;
    /**
     * 错误信息
     */
    private String msg;

    /**
     * 是否已经打印过日志
     */
    private boolean alreadyLogged;


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
        super(joinMessage(errorInfo.getCode(), errorInfo.getDefaultMsg()), th);
        resolve(errorInfo, th, args);
    }

    protected void resolve(ErrorInfo errorInfo, Throwable th, Object[] args) {
        this.codes = new String[]{errorInfo.getCode()};
        this.msg = errorInfo.getDefaultMsg();
        this.args = args;
        if (th instanceof MessageSourceResolvable) {
            MessageSourceResolvable msr = (MessageSourceResolvable) th;
            this.codes = msr.getCodes();
            this.msg = msr.getDefaultMessage();
            this.args = msr.getArguments();
        }
    }


    /**
     * 拼接错误信息
     *
     * @param code       错误码
     * @param defaultMsg 默认错误信息
     * @return 错误信息
     */
    private static String joinMessage(String code, String defaultMsg) {
        if (code == null) {
            return defaultMsg;
        }
        if (defaultMsg == null) {
            return "[" + code + "]";
        }
        return "[" + code + "]" + defaultMsg;
    }


    @Override
    public String[] getCodes() {
        return codes;
    }

    @Override
    public Object[] getArguments() {
        return args;
    }

    @Override
    public String getDefaultMessage() {
        return msg;
    }

    public boolean isAlreadyLogged() {
        return this.alreadyLogged;
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        this.alreadyLogged = true;
        return super.getStackTrace();
    }

}